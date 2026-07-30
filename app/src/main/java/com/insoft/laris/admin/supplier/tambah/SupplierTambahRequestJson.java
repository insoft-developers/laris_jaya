package com.insoft.laris.admin.supplier.tambah;

public class SupplierTambahRequestJson {

    public void setKd_supplier(String kd_supplier) {
        this.kd_supplier = kd_supplier;
    }

    private String kd_supplier;
    private String nm_supplier;
    private String alamat;
    private String kontak;
    private String telepon;

    public void setNm_supplier(String nm_supplier) {
        this.nm_supplier = nm_supplier;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public void setKontak(String kontak) {
        this.kontak = kontak;
    }

    public void setTelepon(String telepon) {
        this.telepon = telepon;
    }
}
