package com.example.product_service.repository;

import com.example.product_service.model.Product;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    public void ProductRepository_Add_ReturnProduct(){
        Product product = Product.builder()
                .name("Shasank")
                .description("ABC")
                .build();

        Product savedProduct = productRepository.save(product);

        Assertions.assertNotNull(savedProduct);
        Assertions.assertTrue(savedProduct.getId()>0);

    }

    @Test
    public void ProductRepository_GetAll_ReturnListOfProduct(){
        Product product1 = Product.builder()
                .name("1")
                .description("ABC")
                .build();
        Product product2 = Product.builder()
                .name("2")
                .description("ABC")
                .build();
        productRepository.save(product1);
        productRepository.save(product2);

        List<Product> products = productRepository.findAll();

        Assertions.assertNotNull(products);
        Assertions.assertEquals(products.size(),2);

    }
//
//    @Test
//    public void ProductRepository_Get_ReturnProduct(){
//        Product product1 = Product.builder()
//                .name("1")
//                .description("ABC")
//                .build();
//        productRepository.save(product1);
//
//        Product product = productRepository.findByProductName(product1.getName());
//
//        Assertions.assertNotNull(product);
//        Assertions.assertEquals(product.getId(),product1.getId());
//    }
//
//    @Test
//    public void ProductRepository_Update_ReturnProduct(){
//        Product product1 = Product.builder()
//                .name("1")
//                .description("ABC")
//                .build();
//        productRepository.save(product1);
//
//        Product productSaved = productRepository.findByProductName(product1.getName());
//        productSaved.setName("11");
//        productSaved.setDescription("abc");
//
//        Product productUpdated = productRepository.save(productSaved);
//
//        Assertions.assertNotNull(productUpdated.getName());
//        Assertions.assertNotNull(productUpdated.getDescription());
//        Assertions.assertEquals(productUpdated.getName(),productSaved.getName());
//    }
//
//    @Test
//    public void ProductRepository_Delete_ReturnProduct(){
//        Product product1 = Product.builder()
//                .name("1")
//                .description("ABC")
//                .build();
//        product1 = productRepository.save(product1);
//
//        productRepository.deleteById(product1.getId());
//
//        Optional<Product> product = productRepository.findById(product1.getId());
//
//        Assertions.assertTrue(product.isEmpty());
//    }
}
