package com.madgarage.api.controller;

import com.madgarage.api.dto.OrderRequest;
import com.madgarage.api.dto.OrderResponse;
import com.madgarage.api.model.Order;
import com.madgarage.api.model.User;
import com.madgarage.api.services.InvoiceService;
import com.madgarage.api.services.OrderService;
import com.madgarage.api.services.UserService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Principal;

/**
 * OrderController is a thin HTTP routing layer.
 * All business logic lives in OrderService and UserService.
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final InvoiceService invoiceService;
    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    public OrderController(OrderService orderService, InvoiceService invoiceService, UserService userService) {
        this.orderService = orderService;
        this.invoiceService = invoiceService;
        this.userService = userService;
    }

    @GetMapping("/my-orders")
    public ResponseEntity<?> getMyOrders(Principal principal) {
        User customer = userService.getCurrentUser(principal.getName());
        return ResponseEntity.ok(orderService.getCustomerOrders(customer));
    }

    @GetMapping("/seller-orders")
    public ResponseEntity<?> getSellerOrders(Principal principal) {
        User seller = userService.getCurrentUser(principal.getName());
        return ResponseEntity.ok(orderService.getSellerOrders(seller));
    }

    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(Principal principal, @RequestBody OrderRequest request) {
        User customer = userService.getCurrentUser(principal.getName());
        OrderResponse response = orderService.placeOrder(customer, request);
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

        com.madgarage.api.model.User currentUser = userService.getCurrentUser(principal.getName());
        boolean isAdmin = currentUser.getRole() == com.madgarage.api.enums.Role.ROLE_ADMIN;
        boolean isSeller = currentUser.getRole() == com.madgarage.api.enums.Role.ROLE_SELLER;
        boolean isOwner = order.getUser() != null && order.getUser().getId().equals(currentUser.getId());

        // For sellers, check if they own any item in the order
        boolean ownsAnyItem = false;
        if (isSeller) {
            ownsAnyItem = order.getItems().stream()
                    .anyMatch(item -> item.getProduct() != null && 
                                     item.getProduct().getSeller() != null && 
                                     item.getProduct().getSeller().getId().equals(currentUser.getId()));
        }

        if (!isAdmin && !isOwner && !ownsAnyItem) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

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

        com.madgarage.api.model.User currentUser = userService.getCurrentUser(principal.getName());
        boolean isAdmin = currentUser.getRole() == com.madgarage.api.enums.Role.ROLE_ADMIN;
        boolean isSeller = currentUser.getRole() == com.madgarage.api.enums.Role.ROLE_SELLER;
        boolean isOwner = order.getUser() != null && order.getUser().getId().equals(currentUser.getId());

        // For sellers, check if they own any item in the order
        boolean ownsAnyItem = false;
        if (isSeller) {
            ownsAnyItem = order.getItems().stream()
                    .anyMatch(item -> item.getProduct() != null && 
                                     item.getProduct().getSeller() != null && 
                                     item.getProduct().getSeller().getId().equals(currentUser.getId()));
        }

        if (!isAdmin && !isOwner && !ownsAnyItem) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        OrderResponse response = orderService.mapToOrderResponse(order, currentUser);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{orderId:[0-9]+}/rating")
    public ResponseEntity<?> postRating(Principal principal, @PathVariable Long orderId, @RequestBody com.madgarage.api.dto.OrderRatingRequest request) {
        User customer = userService.getCurrentUser(principal.getName());
        orderService.saveRating(orderId, request, customer);
        return ResponseEntity.ok().build();
    }
}
