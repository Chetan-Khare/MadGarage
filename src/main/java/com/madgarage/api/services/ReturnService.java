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
    private final RazorpayService razorpayService;

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

        double calculatedRefund = 0.0;
        List<ReturnRequestDto.ReturnItemDto> itemsToReturn = dto.getItems();
        
        // If no items provided, default to full order return
        if (itemsToReturn == null || itemsToReturn.isEmpty()) {
            itemsToReturn = new java.util.ArrayList<>();
            for (com.madgarage.api.model.OrderItem item : order.getItems()) {
                if (item.getProduct() != null && item.getProduct().isReturnable()) {
                    ReturnRequestDto.ReturnItemDto rDto = new ReturnRequestDto.ReturnItemDto();
                    rDto.setOrderItemId(item.getId());
                    rDto.setQuantity(item.getQuantity());
                    itemsToReturn.add(rDto);
                }
            }
        }

        for (ReturnRequestDto.ReturnItemDto itemDto : itemsToReturn) {
            com.madgarage.api.model.OrderItem orderItem = order.getItems().stream()
                    .filter(i -> i.getId().equals(itemDto.getOrderItemId()))
                    .findFirst()
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order item not found"));
            
            if (itemDto.getQuantity() <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Return quantity must be greater than zero");
            }
            
            if (itemDto.getQuantity() > orderItem.getQuantity()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Return quantity exceeds purchased quantity");
            }
            
            com.madgarage.api.model.ReturnRequestItem reqItem = com.madgarage.api.model.ReturnRequestItem.builder()
                    .returnRequest(request)
                    .orderItem(orderItem)
                    .quantity(itemDto.getQuantity())
                    .build();
            request.getItems().add(reqItem);
            
            calculatedRefund += orderItem.getPriceAtPurchase() * itemDto.getQuantity();
        }

        // Apply proportional tax and deduct proportional discounts. Excludes shipping and platform fees.
        double baseCalculated = calculatedRefund;
        if (order.getSubtotal() != null && order.getSubtotal() > 0) {
            double proportion = baseCalculated / order.getSubtotal();
            if (order.getDiscountAmount() != null && order.getDiscountAmount() > 0) {
                calculatedRefund -= (order.getDiscountAmount() * proportion);
            }
            if (order.getTaxAmount() != null && order.getTaxAmount() > 0) {
                calculatedRefund += (order.getTaxAmount() * proportion);
            }
        }

        request.setRefundAmount(calculatedRefund);

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
            restoreOrderStock(request);
        }
        
        return returnRepository.save(request);
    }

    private void restoreOrderStock(ReturnRequest request) {
        Order order = request.getOrder();
        if (request.getItems() != null && !request.getItems().isEmpty()) {
            for (com.madgarage.api.model.ReturnRequestItem reqItem : request.getItems()) {
                if (reqItem.getOrderItem() != null && reqItem.getOrderItem().getProduct() != null && reqItem.getOrderItem().getProduct().isReturnable()) {
                    com.madgarage.api.model.Product p = productRepository.findByIdWithLock(reqItem.getOrderItem().getProduct().getId())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found: " + reqItem.getOrderItem().getProduct().getId()));
                    p.setStockQuantity(p.getStockQuantity() + reqItem.getQuantity());
                    productRepository.save(p);
                }
            }
        } else {
            for (com.madgarage.api.model.OrderItem item : order.getItems()) {
                if (item.getProduct() != null && item.getProduct().isReturnable()) {
                    com.madgarage.api.model.Product p = productRepository.findByIdWithLock(item.getProduct().getId())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found: " + item.getProduct().getId()));
                    p.setStockQuantity(p.getStockQuantity() + item.getQuantity());
                    productRepository.save(p);
                }
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

        if (request.getStatus() == ReturnStatus.REFUNDED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Refund has already been completed for this request");
        }

        Order order = request.getOrder();
        double amountToRefund = request.getRefundAmount() != null ? request.getRefundAmount() : order.getGrandTotal();

        if (order.getPaymentId() != null && !order.getPaymentId().isBlank()) {
            try {
                com.razorpay.Refund rzpRefund = razorpayService.refundPayment(order.getPaymentId(), amountToRefund, "refund_" + order.getId());
                request.setRefundId(rzpRefund.get("id"));
            } catch (com.razorpay.RazorpayException e) {
                request.setStatus(ReturnStatus.REFUND_FAILED);
                request.setAdminNote("Razorpay Refund Failed: " + e.getMessage());
                return returnRepository.save(request);
            }
        }

        request.setStatus(ReturnStatus.REFUNDED);
        request.setResolvedAt(LocalDateTime.now());
        
        // If the order is already marked REFUNDED, this is a retry from a webhook failure.
        // We must avoid restoring stock a second time.
        boolean isRetry = (order.getStatus() == OrderStatus.REFUNDED);

        order.setStatus(OrderStatus.REFUNDED);
        orderRepository.save(order);

        // CRIT-03 FIX: Restore stock for all reasons EXCEPT WRONG_FITMENT (which is restored at pickup).
        // Skip this step if it's a retry, as stock was already restored on the first attempt.
        if (!isRetry && request.getReason() != com.madgarage.api.enums.ReturnReason.WRONG_FITMENT) {
            restoreOrderStock(request);
        }

        return returnRepository.save(request);
    }

    @Transactional
    public ReturnRequest retryRefund(Long returnId) {
        ReturnRequest request = returnRepository.findById(returnId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Return request not found"));
        
        if (request.getStatus() != ReturnStatus.REFUND_FAILED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can only retry if refund failed");
        }
        
        return finalizeRefund(returnId);
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
