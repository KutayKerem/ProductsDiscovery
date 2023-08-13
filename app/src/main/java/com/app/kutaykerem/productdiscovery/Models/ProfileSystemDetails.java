package com.app.kutaykerem.productdiscovery.Models;

public class ProfileSystemDetails {

    String name,deger;

    public ProfileSystemDetails(String name, String deger) {
        this.name = name;
        this.deger = deger;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDeger() {
        return deger;
    }

    public void setDeger(String deger) {
        this.deger = deger;
    }
}
