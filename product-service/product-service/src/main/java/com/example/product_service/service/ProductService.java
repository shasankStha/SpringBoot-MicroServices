package com.example.product_service.service;

import com.example.product_service.dtos.ProductRequest;
import com.example.product_service.model.Product;
import com.example.product_service.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public Product createProduct(ProductRequest productRequest) {
        Product product = Product.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .build();
        product = productRepository.save(product);
        log.info("Product {} is saved",product.getId());
        return product;
    }

    public List<Product> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream().map(product-> mapToProductResponse(product)).toList();
    }

    private Product mapToProductResponse(Product product) {
        return Product.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .build();
    }
}
