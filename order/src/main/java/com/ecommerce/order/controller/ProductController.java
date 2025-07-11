package com.ecommerce.order.controller;

import com.ecommerce.order.model.Product;
import com.ecommerce.order.repository.ProductRepository;
import com.ecommerce.order.service.ProductServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class ProductController {

    private final ProductRepository productRepository;
    private final ProductServiceImpl productServiceImpl;

    public ProductController(ProductRepository productRepository, ProductServiceImpl productServiceImpl) {
        this.productRepository = productRepository;
        this.productServiceImpl = productServiceImpl;
    }


    @GetMapping("/product")
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @PostMapping("/product")
    public ResponseEntity<?> createProduct(@Valid @RequestBody Product product, BindingResult result) {
        if (result.hasErrors()) {
            // return validation errors
            Map<String, String> errors = new HashMap<>();
            result.getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage())
            );
            return ResponseEntity.badRequest().body(errors);
        }

        Product saved = productServiceImpl.saveProduct(product);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @PutMapping("/product/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody Product updatedProduct) {
        return ResponseEntity.ok(productServiceImpl.updateProduct(id, updatedProduct));
    }
}
