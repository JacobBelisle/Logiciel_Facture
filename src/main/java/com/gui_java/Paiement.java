package com.gui_java;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Paiement implements YmlWritable {
    private String date;
    private Float price;

    public Paiement() {
        this.date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        this.price = -1.f;
    }

    public Paiement(String date, Float price) {
        this.date = date;
        this.price = price;
    }

    @Override
    public String toFile() {
        return "\tdate: " + date + '\n' +
                "\tprice: " + price.toString();
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }
}
