package com.ecommerce.order.service;

import com.ecommerce.order.model.Product;

public interface ProductService {

    Product updateProduct(Long id, Product updatedProduct);


}
