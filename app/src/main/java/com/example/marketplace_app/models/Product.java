package com.example.marketplace_app.models;

public class Product {

    private String productId;
    private String productName;
    private String description;
    private double price;
    private String sellerId;

    public Product() {
    }

    public Product(String productId,
                   String productName,
                   String description,
                   double price,
                   String sellerId) {
        this.productId = productId;
        this.productName = productName;
        this.description = description;
        this.price = price;
        this.sellerId = sellerId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }
}