package com.ecommerce.order.service;

import com.ecommerce.order.model.Order;

import java.util.List;

public interface OrderService {
    Order placeOrder(Long productId, String userId, int quantity);
    List<Order> getAllOrders();

}
