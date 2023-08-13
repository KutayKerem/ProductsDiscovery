package com.app.kutaykerem.productdiscovery.Models;

public class KullanıcıBilgileri {

     public String kullanıcıAdı,biyografi,profile;

    public String getKullanıcıAdı() {
        return kullanıcıAdı;
    }

    public void setKullanıcıAdı(String kullanıcıAdı) {
        this.kullanıcıAdı = kullanıcıAdı;
    }

    public String getBiyografi() {
        return biyografi;
    }

    public void setBiyografi(String biyografi) {
        this.biyografi = biyografi;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }



    public KullanıcıBilgileri(String kullanıcıAdı, String biyografi, String profile) {
        this.kullanıcıAdı = kullanıcıAdı;
        this.biyografi = biyografi;
        this.profile = profile;

    }


    public KullanıcıBilgileri() {
    }
}
