package com.app.kutaykerem.productdiscovery.Models;

public class MesajList {
   public  String mesaj ,kullanıcıAdı,profile;

    public String getMesaj() {
        return mesaj;
    }

    public void setMesaj(String mesaj) {
        this.mesaj = mesaj;
    }

    public String getKullanıcıAdı() {
        return kullanıcıAdı;
    }

    public void setKullanıcıAdı(String kullanıcıAdı) {
        this.kullanıcıAdı = kullanıcıAdı;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public MesajList(String mesaj, String kullanıcıAdı, String profile) {
        this.mesaj = mesaj;
        this.kullanıcıAdı = kullanıcıAdı;
        this.profile = profile;
    }
}

