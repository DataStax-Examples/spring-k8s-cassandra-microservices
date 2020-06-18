package com.datastax.examples.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@BasePathAwareController
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @RequestMapping(value = "orders/delete/order", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteOrder(@RequestParam UUID orderId){
        orderRepository.deleteByKeyOrderId(orderId);
        return ResponseEntity.ok(orderId.toString());
    }

    @RequestMapping(value = "orders/delete/product-from-order", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteProductFromOrder(@RequestParam UUID orderId, @RequestParam UUID productId){
        orderRepository.deleteByKeyOrderIdAndKeyProductId(orderId, productId);
        return ResponseEntity.ok(orderId + "," + productId);
    }

    @RequestMapping(value = "orders/add", method = RequestMethod.POST)
    public ResponseEntity<Order> addOrder(@RequestBody Order order){
        orderRepository.save(order);
        return ResponseEntity.ok(order);
    }
}
