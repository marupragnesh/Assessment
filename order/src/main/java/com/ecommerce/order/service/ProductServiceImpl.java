package com.ecommerce.order.service;

import com.ecommerce.order.exception.ResourceNotFoundException;
import com.ecommerce.order.model.Product;
import com.ecommerce.order.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService{
    private final ProductRepository productRepo;

    public ProductServiceImpl(ProductRepository productRepo) {
        this.productRepo = productRepo;
    }

    public Product saveProduct(Product product) {
        return productRepo.save(product);
    }

    @Override
    public Product updateProduct(Long id, Product updatedProduct) {
        Optional<Product> existingProduct = productRepo.findById(id);

        if (existingProduct.isEmpty()) {
            throw new ResourceNotFoundException("Product with ID " + id + " not found.");
        }

        Product product = existingProduct.get();
        product.setName(updatedProduct.getName());
        product.setPrice(updatedProduct.getPrice());
        product.setStock(updatedProduct.getStock());
        product.setUpdatedDate(LocalDateTime.now());

        return productRepo.save(product);
    }

}