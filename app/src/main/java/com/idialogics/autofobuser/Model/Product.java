package com.idialogics.autofobuser.Model;

import java.io.Serializable;

public class Product implements Serializable {

    private String id;

    private String sku, name, keyName, battery, fcc, availability, price, category, image;

    public Product() {
    }

    public Product(String id, String sku, String name, String keyName, String battery, String fcc, String availability, String price, String category, String image) {
        this.id = id;
        this.sku = sku;
        this.name = name;
        this.keyName = keyName;
        this.battery = battery;
        this.fcc = fcc;
        this.availability = availability;
        this.price = price;
        this.category = category;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
