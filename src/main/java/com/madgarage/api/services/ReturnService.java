package com.madgarage.api.services;

import com.madgarage.api.dto.ReturnRequestDto;
import com.madgarage.api.enums.OrderStatus;
import com.madgarage.api.enums.ReturnRequestType;
import com.madgarage.api.enums.ReturnStatus;
import com.madgarage.api.model.Order;
import com.madgarage.api.model.ReturnRequest;
import com.madgarage.api.model.User;
import com.madgarage.api.repository.OrderRepository;
import com.madgarage.api.repository.ProductRepository;
import com.madgarage.api.repository.ReturnRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReturnService {

    private final ReturnRepository returnRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderService orderService;

    @Transactional
    public ReturnRequest createReturnRequest(User user, ReturnRequestDto dto) {
        Order order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        // CRIT-02 FIX: Ownership check to verify customer owns the order
        if (order.getUser() == null || !order.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not own this order.");
        }

        // Validation: Eligibility
        if (order.getStatus() != OrderStatus.DELIVERED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only delivered orders can be returned");
        }

        // Validation: 7-day window from delivery
        LocalDateTime referenceDate = order.getDeliveredAt() != null ? order.getDeliveredAt() : order.getOrderDate();
        if (referenceDate.plusDays(7).isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Return window of 7 days after delivery has expired");
        }

        // Validation: Existing request (Only block if there is a non-rejected request)
        boolean hasActiveRequest = returnRepository.findAllByOrderId(order.getId()).stream()
                .anyMatch(r -> r.getStatus() != ReturnStatus.REJECTED);
        
        if (hasActiveRequest) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "An active return protocol is already in sync for this order");
        }

        // Validation: Returnable items
        boolean hasReturnableItems = order.getItems().stream()
                .anyMatch(item -> item.getProduct() != null && item.getProduct().isReturnable());
        if (!hasReturnableItems) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No items in this order are eligible for return");
        }

        ReturnRequest request = ReturnRequest.builder()
                .order(order)
                .user(user)
                .reason(dto.getReason())
                .requestType(dto.getRequestType())
                .description(dto.getDescription())
                .imageUrls(dto.getImageUrls() != null ? String.join(",", dto.getImageUrls()) : "")
                .status(ReturnStatus.PENDING)
                .requestedAt(LocalDateTime.now())
                .build();

        order.setStatus(OrderStatus.RETURN_REQUESTED);
        orderRepository.save(order);

        return returnRepository.save(request);
    }

    @Transactional
    public ReturnRequest approveReturn(Long returnId, User admin) {
        ReturnRequest request = returnRepository.findById(returnId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Return request not found"));

        if (request.getStatus() != ReturnStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request is not in PENDING state");
        }

        request.setStatus(ReturnStatus.APPROVED);
        Order order = request.getOrder();

        if (request.getRequestType() == ReturnRequestType.REFUND) {
            order.setStatus(OrderStatus.REFUND_IN_PROGRESS);
        }
        // For REPLACEMENT, original order status remains RETURN_REQUESTED until picked up.

        orderRepository.save(order);
        return returnRepository.save(request);
    }

    @Transactional
    public ReturnRequest rejectReturn(Long returnId, String adminNote, User admin) {
        ReturnRequest request = returnRepository.findById(returnId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Return request not found"));

        request.setStatus(ReturnStatus.REJECTED);
        request.setAdminNote(adminNote);
        request.setResolvedAt(LocalDateTime.now());

        Order order = request.getOrder();
        order.setStatus(OrderStatus.DELIVERED); // Revert back to delivered
        orderRepository.save(order);

        return returnRepository.save(request);
    }

    @Transactional
    public ReturnRequest markPickedUp(Long returnId) {
        ReturnRequest request = returnRepository.findById(returnId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Return request not found"));

        if (request.getStatus() != ReturnStatus.APPROVED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only approved returns can be marked as picked up");
        }

        request.setStatus(ReturnStatus.PICKED_UP);
        Order order = request.getOrder();

        if (request.getRequestType() == ReturnRequestType.REPLACEMENT) {
            Order replacementOrder = orderService.createReplacementOrder(order);
            request.setReplacementOrderId(replacementOrder.getId());
            order.setStatus(OrderStatus.REPLACEMENT_SHIPPING);
            orderRepository.save(order);
        }

        // Restore stock if the item is fit for resale (e.g., WRONG_FITMENT)
        if (request.getReason() == com.madgarage.api.enums.ReturnReason.WRONG_FITMENT) {
            restoreOrderStock(order);
        }
        
        return returnRepository.save(request);
    }

    private void restoreOrderStock(Order order) {
        for (com.madgarage.api.model.OrderItem item : order.getItems()) {
            if (item.getProduct() != null && item.getProduct().isReturnable()) {
                // HIGH-04 FIX: Use pessimistic write lock to avoid lost updates under concurrency
                com.madgarage.api.model.Product p = productRepository.findByIdWithLock(item.getProduct().getId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found: " + item.getProduct().getId()));
                p.setStockQuantity(p.getStockQuantity() + item.getQuantity());
                productRepository.save(p);
            }
        }
    }

    @Transactional
    public ReturnRequest finalizeRefund(Long returnId) {
        ReturnRequest request = returnRepository.findById(returnId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Return request not found"));

        if (request.getRequestType() != ReturnRequestType.REFUND) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request is not for refund");
        }

        request.setStatus(ReturnStatus.REFUNDED);
        request.setResolvedAt(LocalDateTime.now());

        Order order = request.getOrder();
        order.setStatus(OrderStatus.REFUNDED);
        orderRepository.save(order);

        // CRIT-03 FIX: Restore stock for all reasons. Since WRONG_FITMENT is already restored in markPickedUp(),
        // we restore stock here for other reasons (e.g. DAMAGED or OTHER returns to be inspected/restocked).
        if (request.getReason() != com.madgarage.api.enums.ReturnReason.WRONG_FITMENT) {
            restoreOrderStock(order);
        }

        return returnRepository.save(request);
    }

    @Transactional(readOnly = true)
    public List<ReturnRequest> getMyReturns(User user) {
        return returnRepository.findByUserId(user.getId());
    }

    @Transactional(readOnly = true)
    public List<ReturnRequest> getAllReturns() {
        return returnRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public List<ReturnRequest> getReturnsByStatus(ReturnStatus status) {
        return returnRepository.findByStatus(status);
    }
}
