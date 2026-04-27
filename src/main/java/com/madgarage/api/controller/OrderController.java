package com.madgarage.api.controller;

import com.madgarage.api.dto.OrderRequest;
import com.madgarage.api.dto.OrderResponse;
import com.madgarage.api.model.Order;
import com.madgarage.api.model.User;
import com.madgarage.api.services.InvoiceService;
import com.madgarage.api.services.OrderService;
import com.madgarage.api.services.UserService;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Principal;

/**
 * OrderController is a thin HTTP routing layer.
 * All business logic lives in OrderService and UserService.
 */
@RestController
@RequestMapping("/api/orders")
@PreAuthorize("isAuthenticated()")
public class OrderController {

    private final OrderService orderService;
    private final InvoiceService invoiceService;
    private final UserService userService;
    private final com.madgarage.api.services.OrderMapper orderMapper;
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    public OrderController(OrderService orderService, InvoiceService invoiceService, UserService userService, com.madgarage.api.services.OrderMapper orderMapper) {
        this.orderService = orderService;
        this.invoiceService = invoiceService;
        this.userService = userService;
        this.orderMapper = orderMapper;
    }

    @GetMapping("/my-orders")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'GARAGE', 'ADMIN')")
    public ResponseEntity<?> getMyOrders(Principal principal) {
        User customer = userService.getCurrentUser(principal.getName());
        return ResponseEntity.ok(orderService.getCustomerOrders(customer));
    }

    @GetMapping("/seller-orders")
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<?> getSellerOrders(Principal principal) {
        User seller = userService.getCurrentUser(principal.getName());
        return ResponseEntity.ok(orderService.getSellerOrders(seller));
    }

    @GetMapping("/garage-fittings")
    @PreAuthorize("hasRole('GARAGE') or hasRole('ADMIN')")
    public ResponseEntity<?> getGarageFittings(Principal principal) {
        User garage = userService.getCurrentUser(principal.getName());
        return ResponseEntity.ok(orderService.getGarageFittings(garage));
    }

    @PostMapping("/checkout")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'GARAGE', 'ADMIN')")
    public ResponseEntity<?> checkout(Principal principal, @Valid @RequestBody OrderRequest request) {
        User customer = userService.getCurrentUser(principal.getName());
        OrderResponse response = orderService.placeOrder(customer, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{orderId:[0-9]+}/verify-payment")
    public ResponseEntity<?> verifyPayment(Principal principal, @PathVariable Long orderId, @RequestParam String paymentId, @RequestParam String signature) {
        User customer = userService.getCurrentUser(principal.getName());
        Order order = orderService.getOrderById(orderId);
        
        if (order == null) return ResponseEntity.notFound().build();
        assertOrderAccess(order, customer);
        
        OrderResponse response = orderService.verifyPayment(orderId, paymentId, signature);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{orderId:[0-9]+}/invoice")
    public ResponseEntity<byte[]> getInvoice(Principal principal, @PathVariable Long orderId) {
        User customer = userService.getCurrentUser(principal.getName());
        Order order = orderService.getOrderById(orderId);

        if (order == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        logger.info("Invoice Request - Order User ID: {}, Requesting Customer ID: {}",
                order.getUser() != null ? order.getUser().getId() : "null",
                customer.getId());

        assertOrderAccess(order, customer);

        byte[] pdf = invoiceService.generateInvoicePdf(order);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment().filename("Invoice_" + orderId + ".pdf").build());
        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
    }

    @GetMapping("/{orderId:[0-9]+}")
    public ResponseEntity<?> getOrderDetails(Principal principal, @PathVariable Long orderId) {
        User customer = userService.getCurrentUser(principal.getName());
        Order order = orderService.getOrderById(orderId);

        if (order == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        assertOrderAccess(order, customer);

        OrderResponse response = orderMapper.mapToOrderResponse(order, customer);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{orderId:[0-9]+}/rating")
    public ResponseEntity<?> postRating(Principal principal, @PathVariable Long orderId, @RequestBody com.madgarage.api.dto.OrderRatingRequest request) {
        User customer = userService.getCurrentUser(principal.getName());
        orderService.saveRating(orderId, request, customer);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{orderId:[0-9]+}/fitting-status")
    @PreAuthorize("hasRole('GARAGE') or hasRole('ADMIN')")
    public ResponseEntity<?> updateFittingStatus(Principal principal, @PathVariable Long orderId, @RequestParam String status) {
        User requester = userService.getCurrentUser(principal.getName());
        Order order = orderService.getOrderById(orderId);

        if (order == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        assertOrderAccess(order, requester);

        OrderResponse response = orderService.updateFittingStatus(orderId, status);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{orderId:[0-9]+}/status")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN', 'GARAGE', 'CUSTOMER')")
    public ResponseEntity<?> updateOrderStatus(Principal principal, @PathVariable Long orderId, @RequestParam String status) {
        User requester = userService.getCurrentUser(principal.getName());
        Order order = orderService.getOrderById(orderId);

        if (order == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        assertOrderAccess(order, requester);

        OrderResponse response = orderService.updateOrderStatus(orderId, status, requester);
        return ResponseEntity.ok(response);
    }

    private void assertOrderAccess(Order order, User currentUser) {
        boolean isAdmin = currentUser.getRole() == com.madgarage.api.enums.Role.ROLE_ADMIN;
        boolean isOwner = order.getUser() != null && order.getUser().getId().equals(currentUser.getId());
        boolean isSeller = currentUser.getRole() == com.madgarage.api.enums.Role.ROLE_SELLER;
        boolean ownsAnyItem = isSeller && order.getItems().stream()
            .anyMatch(i -> i.getProduct() != null 
                        && i.getProduct().getSeller() != null
                        && i.getProduct().getSeller().getId().equals(currentUser.getId()));

        boolean isGaragePartner = (currentUser.getRole() == com.madgarage.api.enums.Role.ROLE_GARAGE || currentUser.getRole() == com.madgarage.api.enums.Role.ROLE_ADMIN) 
            && order.getFittingGarageId() != null 
            && order.getFittingGarageId().equals(currentUser.getId());

        if (!isAdmin && !isOwner && !ownsAnyItem && !isGaragePartner) {
            throw new org.springframework.web.server.ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied.");
        }
    }
}
