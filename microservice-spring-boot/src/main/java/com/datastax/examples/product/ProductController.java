package com.datastax.examples.product;

import java.util.UUID;

import com.datastax.examples.product.Product;
import com.datastax.examples.product.ProductService;
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
        return ResponseEntity.ok(productService.find(name));
    }

    @GetMapping("products/search/{name}/{id}")
    public ResponseEntity<Product> findProductsByNameAndId(@PathVariable String name, @PathVariable UUID id) {
        Product product = productService.find(name, id);
        return ResponseEntity.ok(product);
    }

    @PostMapping("products/add")
    public ResponseEntity<String> addProduct(@RequestBody Product product){
        productService.add(product);
        return ResponseEntity.ok(product.getName());
    }

    @DeleteMapping("products/delete/{name}")
    public ResponseEntity<String> removeProductByName(@PathVariable String name){
        productService.remove(name);
        return ResponseEntity.ok(name);

    }

    @DeleteMapping("products/delete/{name}/{id}")
    public ResponseEntity<String> removeProductByNameAndId(@PathVariable String name, @PathVariable UUID id){
        productService.remove(name, id);
        return ResponseEntity.ok(name + "," + id);
    }

}
