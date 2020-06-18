package com.datastax.examples.product;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    @Autowired
    private ProductDao productDao;

    public void add(Product product) {
        productDao.addProduct(product);
    }

    public void remove(String productName) {
        productDao.deleteByName(productName);
    }

    public void remove(String productName, UUID id) {
        productDao.deleteByNameAndId(productName, id);
    }

    public Iterable<Product> find(String name) {
        return productDao.findByName(name);
    }

    public Product find(String name, UUID id) {
        return productDao.findByNameAndId(name, id);
    }
}