package com.example.aqua_v2.model;

public class TemperatureSensor {
    private String datetime;
    private String value;

    public TemperatureSensor(String datetime, String value) {
        this.datetime = datetime;
        this.value = value;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
