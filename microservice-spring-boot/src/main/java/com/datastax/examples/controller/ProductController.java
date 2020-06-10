package com.datastax.examples.controller;

import java.util.UUID;

import com.datastax.examples.model.Product;
import com.datastax.examples.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("products/search/{name}")
    public ResponseEntity<Iterable<Product>> findProductsByName(@PathVariable String name) {
        return new ResponseEntity<Iterable<Product>>(productService.find(name), HttpStatus.OK);
    }

    @GetMapping("products/search/{name}/{id}")
    public ResponseEntity<Product> findProductsByNameAndId(@PathVariable String name, @PathVariable UUID id) {
        Product product = productService.find(name, id);
        return new ResponseEntity<Product>(product, HttpStatus.OK);
    }

    @PostMapping("products/add")
    public ResponseEntity<String> addProduct(@RequestBody Product product){
        productService.add(product);
        return new ResponseEntity<String>(product.getName(), HttpStatus.OK);
    }

    @DeleteMapping("products/delete/{name}")
    public ResponseEntity<String> removeProductByName(@PathVariable String name){
        productService.remove(name);
        return new ResponseEntity<String>(name, HttpStatus.OK);

    }

    @DeleteMapping("products/delete/{name}/{id}")
    public ResponseEntity<String> removeProductByNameAndId(@PathVariable String name, @PathVariable UUID id){
        productService.remove(name, id);
        return new ResponseEntity<String>(name + " " + id, HttpStatus.OK);
    }

}
