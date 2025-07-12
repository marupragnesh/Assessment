package com.ecommerce.order.controller;

import com.ecommerce.order.model.Order;
import com.ecommerce.order.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class OrderController {
    static class OrderRequest {
        public Long productId;
        public String userId;
        public int quantity;
    }
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }


    @PostMapping("/order")
    public Order placeOrder(@RequestBody OrderRequest request) {
        return orderService.placeOrder(request.productId, request.userId, request.quantity);
    }

    @GetMapping("/order")
    public ResponseEntity<?> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }




}
