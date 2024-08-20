package com.example.product_service.controller;

import com.example.product_service.dtos.ProductRequest;
import com.example.product_service.dtos.ProductResponse;
import com.example.product_service.model.Product;
import com.example.product_service.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    private Product product;
    private ProductResponse productResponse;

    @BeforeEach
    public void init(){
        product = Product.builder().name("Shasank").description("ABC").build();
        productResponse = productResponse.builder().name("Shasank").description("ABC").build();
    }


    @Test
    public void ProductController_AddProduct_ReturnCreated() throws Exception{
                when(productService.createProduct(any(ProductRequest.class))).thenReturn(product);

        mockMvc.perform(post("/api/product")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .content(objectMapper.writeValueAsString(productResponse)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("name").value("Shasank"))
                .andExpect(jsonPath("description").value("ABC"));

    }

    @Test
    public void ProductController_GetAllProduct_ReturnResponseListOfProduct() throws Exception{
        List<Product> users = new ArrayList<>();
        users.add(product);
        users.add(Product.builder().name("ABC").description("123").build());

        when(productService.getAllProducts()).thenReturn(users);

        mockMvc.perform(get("/api/product"))
                .andExpect(jsonPath("[0]name").value("Shasank"))
                .andExpect(jsonPath("[0]description").value("ABC"))
                .andExpect(status().isOk());

    }

//
//    @Test
//    public void ProductController_GetProduct_ReturnResponseProduct() throws Exception{
//
//        when(productService.getProductByProductname(eq(product.getProductName()))).thenReturn(product);
//
//        mockMvc.perform(get("/user/{name}","Shasank"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("name").value("Shasank"))
//                .andExpect(jsonPath("description").value("ABC"));
//
//    }
//
//    @Test
//    public void ProductController_UpdateProduct_ReturnUpdated() throws Exception{
//        Product userUpdated = Product.builder().name("Shanks").description("Shanks").build();
//
//        when(productService.updateProduct(any(productResponse.class),eq(product.getProductName()))).thenReturn(userUpdated);
//
//        mockMvc.perform(put("/user/{name}","Shasank")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .content(objectMapper.writeValueAsString(productResponse)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("name").value("Shanks"))
//                .andExpect(jsonPath("description").value("Shanks"));
//
//    }
//
//    @Test
//    public void ProductController_DeleteProduct_ReturnResponseOk() throws Exception{
//
//        when(productService.deleteProduct(eq(product.getProductName()))).thenReturn(0);
//
//        mockMvc.perform(delete("/user/{name}","Shasank"))
//                .andExpect(status().isOk());
//
//    }


}
