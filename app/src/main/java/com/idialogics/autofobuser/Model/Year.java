package com.idialogics.autofobuser.Model;

import java.io.Serializable;

public class Year implements Serializable {
    private String year, id, parentId, productId;

    public Year() {
    }

    public Year(String id, String year, String parentId, String productId) {
        this.year = year;
        this.id = id;
        this.parentId = parentId;
        this.productId = productId;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    @Override
    public String toString() {
        return year;
    }
}
