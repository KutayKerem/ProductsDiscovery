package com.app.kutaykerem.productdiscovery.Models;

public class UserSohbetList {

    String hedefId,mesaj;

    public String getHedefId() {
        return hedefId;
    }

    public void setHedefId(String hedefId) {
        this.hedefId = hedefId;
    }

    public String getMesaj() {
        return mesaj;
    }

    public void setMesaj(String mesaj) {
        this.mesaj = mesaj;
    }

    public UserSohbetList() {
    }

    public UserSohbetList(String hedefId, String mesaj) {
        this.hedefId = hedefId;
        this.mesaj = mesaj;
    }
}
