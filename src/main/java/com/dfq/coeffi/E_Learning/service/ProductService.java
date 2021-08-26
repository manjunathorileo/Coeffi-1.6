package com.dfq.coeffi.E_Learning.service;

import com.dfq.coeffi.E_Learning.modules.Product;

import java.util.ArrayList;
import java.util.Optional;

public interface ProductService {

    Product saveProduct(Product product);

    Optional<Product> getProductById(long id);

    void deActivate(long id);

    ArrayList<Product> getProductByStatus(boolean status);

}
