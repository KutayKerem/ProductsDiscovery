package com.app.kutaykerem.productdiscovery.Models;

public class Sohbetlerlistesidetails {


    String type;
    Boolean seen;
    String time;
    String mesaj;
    String from;
    String hedefId;
    String kullanıcıAdı;
    String profile;

    public Sohbetlerlistesidetails(String type, Boolean seen, String time, String mesaj, String from, String hedefId, String kullanıcıAdı, String profile) {
        this.type = type;
        this.seen = seen;
        this.time = time;
        this.mesaj = mesaj;
        this.from = from;
        this.hedefId = hedefId;
        this.kullanıcıAdı = kullanıcıAdı;
        this.profile = profile;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getSeen() {
        return seen;
    }

    public void setSeen(Boolean seen) {
        this.seen = seen;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMesaj() {
        return mesaj;
    }

    public void setMesaj(String mesaj) {
        this.mesaj = mesaj;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getHedefId() {
        return hedefId;
    }

    public void setHedefId(String hedefId) {
        this.hedefId = hedefId;
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

    public Sohbetlerlistesidetails() {
    }
}

