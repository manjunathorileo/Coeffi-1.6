package com.dfq.coeffi.E_Learning.service;

import com.dfq.coeffi.E_Learning.modules.Product;
import com.dfq.coeffi.E_Learning.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service(value = "Product")
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public Product saveProduct(Product product) {
        product.status = true;
        return productRepository.save(product);
    }

    @Override
    public Optional<Product> getProductById(long id) {
        return Optional.ofNullable(productRepository.findOne(id));
    }

    @Override
    public void deActivate(long id) {
        productRepository.deActivate(id);
    }

    @Override
    public ArrayList<Product> getProductByStatus(boolean status) {
        return productRepository.findByStatus(status);
    }
}





