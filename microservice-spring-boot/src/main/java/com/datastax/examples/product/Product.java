package com.datastax.examples.product;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;


public class Product {

    private UUID id;

    private String name;

    private String description;

    private BigDecimal price;

    private Instant lastUpdated;

    public Product(String name, UUID id, String description, BigDecimal price, Instant lastUpdated){
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.lastUpdated = lastUpdated;
    }

    public Instant getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Instant last_updated) {
        this.lastUpdated = last_updated;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
