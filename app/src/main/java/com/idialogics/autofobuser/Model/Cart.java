package com.idialogics.autofobuser.Model;

import java.io.Serializable;

public class Cart implements Serializable {

    private String id, manufacturer, model, year, productId, productImage, count, productName, price, battery, fcc, availability, date;

    public Cart() {
    }

    public Cart(String id, String manufacturer, String model, String year, String productId, String productImage, String count, String productName, String price, String battery, String fcc, String availability, String date) {
        this.id = id;
        this.manufacturer = manufacturer;
        this.model = model;
        this.year = year;
        this.productId = productId;
        this.productImage = productImage;
        this.count = count;
        this.productName = productName;
        this.price = price;
        this.battery = battery;
        this.fcc = fcc;
        this.availability = availability;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getBattery() {
        return battery;
    }

    public void setBattery(String battery) {
        this.battery = battery;
    }

    public String getFcc() {
        return fcc;
    }

    public void setFcc(String fcc) {
        this.fcc = fcc;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
