package com.app.kutaykerem.productdiscovery.Models;

public class AnakartÖzelliklerDetails {

    String anakartÖzellikleriName , degeri;

    public AnakartÖzelliklerDetails(String anakartÖzellikleriName, String degeri) {
        this.anakartÖzellikleriName = anakartÖzellikleriName;
        this.degeri = degeri;
    }

    public String getAnakartÖzellikleriName() {
        return anakartÖzellikleriName;
    }

    public void setAnakartÖzellikleriName(String anakartÖzellikleriName) {
        this.anakartÖzellikleriName = anakartÖzellikleriName;
    }

    public String getDegeri() {
        return degeri;
    }

    public void setDegeri(String degeri) {
        this.degeri = degeri;
    }
}
