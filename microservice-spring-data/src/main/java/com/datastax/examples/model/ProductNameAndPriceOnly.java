package com.datastax.examples.model;

import org.springframework.data.rest.core.config.Projection;

import java.time.Instant;

@Projection(name = "product-name-and-price", types = { Order.class })
public interface ProductNameAndPriceOnly {
    String getProductName();
    Float getProductPrice();
}
