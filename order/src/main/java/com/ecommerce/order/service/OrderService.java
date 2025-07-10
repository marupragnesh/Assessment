package com.ecommerce.order.service;

import com.ecommerce.order.model.Order;

public interface OrderService {
    Order placeOrder(Long productId, String userId, int quantity);
}
