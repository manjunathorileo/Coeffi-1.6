package com.dfq.coeffi.E_Learning.controller;

import com.dfq.coeffi.E_Learning.modules.Product;
import com.dfq.coeffi.E_Learning.service.ProductService;
import com.dfq.coeffi.controller.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Optional;

@RestController
@Slf4j
public class ProductController extends BaseController {

    @Autowired
    private ProductService productService;

    /**
     * @return create, save and update product
     */

    @PostMapping(value = "/product")
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product persistedobjt = productService.saveProduct(product);
        return new ResponseEntity<>(persistedobjt, HttpStatus.CREATED);
    }

    /**
     * @return
     */

    @GetMapping(value = "/product")
    public ResponseEntity<ArrayList<Product>> getActiveProduct() {
        ArrayList<Product> productArrayList = productService.getProductByStatus(true);
        if (CollectionUtils.isEmpty(productArrayList)) {
            //no product found
        }
        return new ResponseEntity<>(productArrayList, HttpStatus.OK);
    }

    /**
     * @return
     */

    @GetMapping(value = "/product/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable long id) {
        Optional<Product> productOptional = productService.getProductById(id);
        if (!productOptional.isPresent()) {
            throw new EntityNotFoundException("No Product found for id " + id);
        }
        Product product = productOptional.get();
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    /**
     * @return
     */

    @DeleteMapping("/product/{id}")
    public ResponseEntity<Product> deleteProductById(@PathVariable long id) {
        productService.deActivate(id);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }


}
