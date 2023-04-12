package com.example.aqua_v2.model;

public class InfoModel {
    private String level;
    private String status;

    public InfoModel(String level, String status) {
        this.level = level;
        this.status = status;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
