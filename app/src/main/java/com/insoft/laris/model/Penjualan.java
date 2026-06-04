package com.insoft.laris.model;

public class Penjualan {
    private int id;
    private String kd_barang;
    private String barcode;
    private String nm_barang;
    private String satuan;
    private int jumlah;
    private int harga;
    private int modal;
    private int total;
    private String kd_user;
    private int status;
    private int id_hold;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKd_barang() {
        return kd_barang;
    }

    public void setKd_barang(String kd_barang) {
        this.kd_barang = kd_barang;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getNm_barang() {
        return nm_barang;
    }

    public void setNm_barang(String nm_barang) {
        this.nm_barang = nm_barang;
    }

    public String getSatuan() {
        return satuan;
    }

    public void setSatuan(String satuan) {
        this.satuan = satuan;
    }

    public int getJumlah() {
        return jumlah;
    }

    public void setJumlah(int jumlah) {
        this.jumlah = jumlah;
    }

    public int getHarga() {
        return harga;
    }

    public void setHarga(int harga) {
        this.harga = harga;
    }

    public int getModal() {
        return modal;
    }

    public void setModal(int modal) {
        this.modal = modal;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getKd_user() {
        return kd_user;
    }

    public void setKd_user(String kd_user) {
        this.kd_user = kd_user;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getId_hold() {
        return id_hold;
    }

    public void setId_hold(int id_hold) {
        this.id_hold = id_hold;
    }
}


