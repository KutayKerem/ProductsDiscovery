package com.app.kutaykerem.productdiscovery.Models;

public class YorumlarDetails {

    public String kullanıcıAdı,gonderenId,gonderiId,yorum , yorumId,profile,tarih;


    public YorumlarDetails(String kullanıcıAdı, String gonderenId, String gonderiId, String yorum, String yorumId, String profile, String tarih) {
        this.kullanıcıAdı = kullanıcıAdı;
        this.gonderenId = gonderenId;
        this.gonderiId = gonderiId;
        this.yorum = yorum;
        this.yorumId = yorumId;
        this.profile = profile;
        this.tarih = tarih;
    }

    public YorumlarDetails() {
    }

    public String getKullanıcıAdı() {
        return kullanıcıAdı;
    }

    public void setKullanıcıAdı(String kullanıcıAdı) {
        this.kullanıcıAdı = kullanıcıAdı;
    }

    public String getGonderenId() {
        return gonderenId;
    }

    public void setGonderenId(String gonderenId) {
        this.gonderenId = gonderenId;
    }

    public String getGonderiId() {
        return gonderiId;
    }

    public void setGonderiId(String gonderiId) {
        this.gonderiId = gonderiId;
    }

    public String getYorum() {
        return yorum;
    }

    public void setYorum(String yorum) {
        this.yorum = yorum;
    }

    public String getYorumId() {
        return yorumId;
    }

    public void setYorumId(String yorumId) {
        this.yorumId = yorumId;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getTarih() {
        return tarih;
    }

    public void setTarih(String tarih) {
        this.tarih = tarih;
    }
}

