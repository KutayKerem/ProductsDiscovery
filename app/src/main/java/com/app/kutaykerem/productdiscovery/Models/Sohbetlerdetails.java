package com.app.kutaykerem.productdiscovery.Models;

public class Sohbetlerdetails {



 public  String type;
 public  Boolean seen;
    String time;

    String mesaj;
    String mesajId;
    String from;
    String hedefId;
    String kullanıcıAdı;
    String profile;
    String farklıKullanici;



    public Sohbetlerdetails(String type, Boolean seen, String time, String mesaj, String mesajId, String from, String hedefId, String kullanıcıAdı, String profile, String farklıKullanici) {
        this.type = type;
        this.seen = seen;
        this.time = time;
        this.mesaj = mesaj;
        this.mesajId = mesajId;
        this.from = from;
        this.hedefId = hedefId;
        this.kullanıcıAdı = kullanıcıAdı;
        this.profile = profile;
        this.farklıKullanici = farklıKullanici;
    }

    public Sohbetlerdetails() {
    }
    public String getFarklıKullanici() {
        return farklıKullanici;
    }

    public void setFarklıKullanici(String farklıKullanici) {
        this.farklıKullanici = farklıKullanici;
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

    public String getMesajId() {
        return mesajId;
    }

    public void setMesajId(String mesajId) {
        this.mesajId = mesajId;
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
}
