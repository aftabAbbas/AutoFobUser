package com.idialogics.autofobuser.Model;

import java.io.Serializable;

public class Manufacturer implements Serializable {
    private String name, id;

    public Manufacturer() {
    }

    public Manufacturer(String id, String name) {
        this.name = name;
        this.id = id;
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


    @Override
    public String toString() {
        return name;
    }
}
