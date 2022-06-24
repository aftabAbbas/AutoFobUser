package com.idialogics.autofobuser.Model;

import java.io.Serializable;

public class Model implements Serializable {
    private String name, id, parentId;

    public Model() {
    }

    public Model(String id, String name, String parentId) {
        this.name = name;
        this.id = id;
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    @Override
    public String toString() {
        return name;
    }
}
