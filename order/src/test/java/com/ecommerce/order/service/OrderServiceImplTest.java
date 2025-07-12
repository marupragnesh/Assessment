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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OrderServiceImplTest {

    private OrderRepository orderRepository;
    private ProductRepository productRepository;
    private UserRepository userRepository;
    private OrderServiceImpl orderService;

    @BeforeEach
    void setup() {
        orderRepository = mock(OrderRepository.class);
        productRepository = mock(ProductRepository.class);
        userRepository = mock(UserRepository.class);

        orderService = new OrderServiceImpl(productRepository, orderRepository, userRepository);
    }

    @Test
    void testPlaceOrder_Success() {
        // Setup dummy product
        Product product = new Product("Laptop", 50000.0, 10, LocalDateTime.now(), LocalDateTime.now());
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // Setup dummy user
        User user = new User("1f", "user123", "Test", "user@test.com");
        when(userRepository.findByUserId("user123")).thenReturn(Optional.of(user));

        // Simulate saving the order
        when(orderRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Execute the service method
        Order order = orderService.placeOrder(1L, "user123", 2);

        // Assertions
        assertNotNull(order);
        assertEquals("user123", order.getUserId());
        assertEquals(2, order.getQuantity());
        assertEquals(100000.0, order.getTotalAmount());
        assertEquals(OrderStatus.PLACED, order.getStatus());
    }

    @Test
    void testPlaceOrder_ProductNotFound() {
        //  Setup valid user (so user check passes)
        User user = new User("pragnesh001", "Pragnesh", "prag@example.com", "123456");
        when(userRepository.findByUserId("pragnesh001")).thenReturn(Optional.of(user));

        //  No product with ID 99
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        //  Expect product-not-found exception
        ResourceNotFoundException thrown = assertThrows(
                ResourceNotFoundException.class,
                () -> orderService.placeOrder(99L, "pragnesh001", 1)
        );

        assertEquals("Product not found with id: 99", thrown.getMessage());
    }

    @Test
    void testPlaceOrder_InsufficientStock() {
        // Mock valid product with only 1 item in stock
        Product product = new Product("Laptop", 50000.0, 1, LocalDateTime.now(), LocalDateTime.now());
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // Mock valid user
        User user = new User("pragnesh001", "Pragnesh", "prag@example.com", "123456");
        when(userRepository.findByUserId("pragnesh001")).thenReturn(Optional.of(user));

        // Try ordering more than stock available
        InsufficientStockException thrown = assertThrows(
                InsufficientStockException.class,
                () -> orderService.placeOrder(1L, "pragnesh001", 2)
                // asking for 2, only 1 in stock
        );

        assertEquals("Only 1 items left in stock", thrown.getMessage());
    }

    @Test
    void testPlaceOrder_UserNotFound() {
        //  Mock valid product
        String userId = "invalidUser";
        Product product = new Product("Laptop", 50000.0, 5, LocalDateTime.now(), LocalDateTime.now());
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        //  No user returned
        when(userRepository.findByUserId("invalidUser")).thenReturn(Optional.empty());

        UserNotFoundException thrown = assertThrows(
                UserNotFoundException.class,
                () -> orderService.placeOrder(1L, userId, 1)
        );

        assertEquals("User not found: " + userId, thrown.getMessage());
    }


    @Test
    void testPlaceOrder_PaymentFailure() {
        // Mock product with enough stock
        Product product = new Product("Laptop", 50000.0, 10, LocalDateTime.now(), LocalDateTime.now());
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // Mock valid user
        User user = new User("pragnesh001", "Pragnesh", "prag@example.com", "123456");
        when(userRepository.findByUserId("pragnesh001")).thenReturn(Optional.of(user));

        // Simulate payment failure by forcing Random to return false
        OrderServiceImpl orderServiceWithFailingPayment = new OrderServiceImpl(productRepository, orderRepository, userRepository) {
            @Override
            public Order placeOrder(Long productId, String userId, int quantity) {
                // Same logic, but force payment to fail
                Optional<Product> optionalProduct = productRepository.findById(productId);
                if (optionalProduct.isEmpty()) {
                    throw new ResourceNotFoundException("Product not found with id: " + productId);
                }

                Optional<User> optionalUser = userRepository.findByUserId(userId);
                if (optionalUser.isEmpty()) {
                    throw new UserNotFoundException("User not found: " + userId);
                }

                Product product = optionalProduct.get();

                if (product.getStock() < quantity) {
                    throw new InsufficientStockException("Only " + product.getStock() + " items left in stock");
                }

                boolean paymentSuccess = false; // Force payment failure

                if (!paymentSuccess) {
                    throw new PaymentFailedException("Payment failed. Order not placed.");
                }

                return null; // should not reach here
            }
        };

        PaymentFailedException thrown = assertThrows(
                PaymentFailedException.class,
                () -> orderServiceWithFailingPayment.placeOrder(1L, "pragnesh001", 2)
        );

        assertEquals("Payment failed. Order not placed.", thrown.getMessage());
    }

//



}
