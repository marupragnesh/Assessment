package com.ecommerce.order.service;

import com.ecommerce.order.exception.InsufficientStockException;
import com.ecommerce.order.exception.PaymentFailedException;
import com.ecommerce.order.exception.ResourceNotFoundException;
import com.ecommerce.order.exception.UserNotFoundException;
import com.ecommerce.order.model.Order;
import com.ecommerce.order.model.OrderStatus;
import com.ecommerce.order.model.Product;
import com.ecommerce.order.model.User;
import com.ecommerce.order.repository.OrderRepository;
import com.ecommerce.order.repository.ProductRepository;
import com.ecommerce.order.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class OrderServiceImpl implements OrderService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public OrderServiceImpl(ProductRepository productRepository, OrderRepository orderRepository, UserRepository userRepository) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public Order placeOrder(Long productId, String userId, int quantity) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }

        Optional<User> user = userRepository.findByUserId(userId);
        if (user.isEmpty()) {
            throw new UserNotFoundException("User not found: " + userId);
        }

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        Optional<Product> optionalProduct = productRepository.findById(productId);
// check product is available or not
        if (optionalProduct.isEmpty()) {
            throw new ResourceNotFoundException("Product not found with id: " + productId);
        }


// if available than get product
        Product product = optionalProduct.get();
        // check single thread access and check stock of product
        synchronized (this) {
            if (product.getStock() < quantity) {
                throw new InsufficientStockException("Only " + product.getStock() + " items left in stock");
            }

// if stock available than save order and update stock
            product.setStock(product.getStock() - quantity);
            product.setUpdatedDate(LocalDateTime.now());
            productRepository.save(product);
        }
// payment simulation failed or pass.
        boolean paymentSuccess = new Random().nextBoolean();

        if (!paymentSuccess) {
            throw new PaymentFailedException("Payment failed. Order not placed.");
        }

        Order order = new Order();
        order.setUserId(userId);
        order.setQuantity(quantity);
        order.setProduct(product);
        order.setCreatedAt(LocalDateTime.now());
        order.setTotalAmount(product.getPrice() * quantity);
        order.setStatus(paymentSuccess ? OrderStatus.PLACED : OrderStatus.FAILED);

        return orderRepository.save(order);
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

}
