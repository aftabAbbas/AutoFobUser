package com.idialogics.autofobuser.Model;

import java.io.Serializable;

public class Notifications implements Serializable {

    private String id, title, body, time, status;

    public Notifications() {
    }

    public Notifications(String id, String title, String body, String time, String status) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.time = time;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
