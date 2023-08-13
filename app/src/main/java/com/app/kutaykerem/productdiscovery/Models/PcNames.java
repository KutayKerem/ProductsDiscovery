package com.app.kutaykerem.productdiscovery.Models;

import androidx.annotation.Keep;

import java.io.Serializable;

@Keep
public class PcNames implements Serializable {

   public String name;
    public int image,sayi;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public int getSayi() {
        return sayi;
    }

    public void setSayi(int sayi) {
        this.sayi = sayi;
    }

    public PcNames(String name, int image, int sayi) {
        this.name = name;
        this.image = image;
        this.sayi = sayi;
    }
}
