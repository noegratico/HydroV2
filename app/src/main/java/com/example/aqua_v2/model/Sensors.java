package com.example.aqua_v2.model;

public class Sensors {

    String value_hum;
    String value_lss;
    String value_tem;
    public Sensors(String value_tem, String value_hum, String value_lss) {
        this.value_tem = value_tem;
        this.value_hum = value_hum;
        this.value_lss = value_lss;
    }
    public String getValue_tem() {
        return value_tem;
    }

    public void setValue_tem(String value_tem) {
        this.value_tem = value_tem;
    }

    public String getValue_hum() {
        return value_hum;
    }

    public void setValue_hum(String value_hum) {
        this.value_hum = value_hum;
    }

    public String getValue_lss() {
        return value_lss;
    }

    public void setValue_lss(String value_lss) {
        this.value_lss = value_lss;
    }





}
