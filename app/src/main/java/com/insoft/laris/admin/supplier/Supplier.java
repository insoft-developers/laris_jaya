package com.insoft.laris.admin.supplier;

public class Supplier {
    private String kd_supplier;
    private String nm_supplier;
    private String alamat;
    private String kontak;
    private String telepon;


    public String getKd_supplier() {
        return kd_supplier;
    }

    public void setKd_supplier(String kd_supplier) {
        this.kd_supplier = kd_supplier;
    }

    public String getNm_supplier() {
        return nm_supplier;
    }

    public void setNm_supplier(String nm_supplier) {
        this.nm_supplier = nm_supplier;
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

    @Override
    public String toString() {
        return nm_supplier == null ? "" : nm_supplier;
    }
}
