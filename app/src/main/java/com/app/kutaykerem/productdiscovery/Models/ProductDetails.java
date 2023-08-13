package com.app.kutaykerem.productdiscovery.Models;

public class ProductDetails {


    String name , detailsName;

    public ProductDetails(String name, String detailsName) {
        this.name = name;
        this.detailsName = detailsName;
    }

    public ProductDetails() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetailsName() {
        return detailsName;
    }

    public void setDetailsName(String detailsName) {
        this.detailsName = detailsName;
    }
}
