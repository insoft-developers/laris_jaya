package com.insoft.laris.model;

public class Pelanggan {
    private int id;
    private String kd_pelanggan;
    private String nm_pelanggan;
    private String alamat;
    private String kontak;
    private String telepon;
    private String grup;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKd_pelanggan() {
        return kd_pelanggan;
    }

    public void setKd_pelanggan(String kd_pelanggan) {
        this.kd_pelanggan = kd_pelanggan;
    }

    public String getNm_pelanggan() {
        return nm_pelanggan;
    }

    public void setNm_pelanggan(String nm_pelanggan) {
        this.nm_pelanggan = nm_pelanggan;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getKontak() {
        return kontak;
    }

    public void setKontak(String kontak) {
        this.kontak = kontak;
    }

    public String getTelepon() {
        return telepon;
    }

    public void setTelepon(String telepon) {
        this.telepon = telepon;
    }

    public String getGrup() {
        return grup;
    }

    public void setGrup(String grup) {
        this.grup = grup;
    }
}
