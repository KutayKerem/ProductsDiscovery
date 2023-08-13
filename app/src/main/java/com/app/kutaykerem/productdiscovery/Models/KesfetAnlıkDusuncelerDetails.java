package com.app.kutaykerem.productdiscovery.Models;


import com.google.firebase.Timestamp;

public class KesfetAnlıkDusuncelerDetails {


    public String parcaAdi,aciklama,puan,gonderiId,gonderenId,parcaModeli,ayrıParca;
    Timestamp  tarih;
    int BegeniSayisi;

    public KesfetAnlıkDusuncelerDetails(String parcaAdi, String aciklama, String puan, String gonderiId, String gonderenId, String parcaModeli, String ayrıParca, Timestamp tarih, int begeniSayisi) {
        this.parcaAdi = parcaAdi;
        this.aciklama = aciklama;
        this.puan = puan;
        this.gonderiId = gonderiId;
        this.gonderenId = gonderenId;
        this.parcaModeli = parcaModeli;
        this.ayrıParca = ayrıParca;
        this.tarih = tarih;
        this.BegeniSayisi = begeniSayisi;
    }


    public String getParcaAdi() {
        return parcaAdi;
    }

    public void setParcaAdi(String parcaAdi) {
        this.parcaAdi = parcaAdi;
    }

    public String getAciklama() {
        return aciklama;
    }

    public void setAciklama(String aciklama) {
        this.aciklama = aciklama;
    }

    public String getPuan() {
        return puan;
    }

    public void setPuan(String puan) {
        this.puan = puan;
    }



    public String getGonderiId() {
        return gonderiId;
    }

    public void setGonderiId(String gonderiId) {
        this.gonderiId = gonderiId;
    }

    public String getGonderenId() {
        return gonderenId;
    }

    public void setGonderenId(String gonderenId) {
        this.gonderenId = gonderenId;
    }

    public String getParcaModeli() {
        return parcaModeli;
    }

    public void setParcaModeli(String parcaModeli) {
        this.parcaModeli = parcaModeli;
    }

    public String getAyrıParca() {
        return ayrıParca;
    }

    public void setAyrıParca(String ayrıParca) {
        this.ayrıParca = ayrıParca;
    }

    public Timestamp getTarih() {
        return tarih;
    }

    public void setTarih(Timestamp tarih) {
        this.tarih = tarih;
    }

    public int getBegeniSayisi() {
        return BegeniSayisi;
    }

    public void setBegeniSayisi(int begeniSayisi) {
        BegeniSayisi = begeniSayisi;
    }
}
