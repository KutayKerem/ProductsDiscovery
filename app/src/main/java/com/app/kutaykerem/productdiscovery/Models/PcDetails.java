package com.app.kutaykerem.productdiscovery.Models;

public class PcDetails {


    String name , detailsName;

    public PcDetails(String name, String detailsName) {
        this.name = name;
        this.detailsName = detailsName;
    }

    public PcDetails() {
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
