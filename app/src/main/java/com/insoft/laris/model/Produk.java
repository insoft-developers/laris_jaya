package com.insoft.laris.model;

public class Produk {
    private String kd_barang;
    private String barcode;
    private String nm_barang;
    private String kd_kategori;
    private int harga_beli;
    private int harga_jual;
    private String satuan;
    private int stok;
    private int konversi;
    private int hj;
    private int harga_member;
    private int diskon_member;
    private String kd_supplier;
    private int diskon;
    private int harga_freelance;
    private int harga_karton_freelance;

    public int getHarga_reseller() {
        return harga_reseller;
    }

    public void setHarga_reseller(int harga_reseller) {
        this.harga_reseller = harga_reseller;
    }

    private int harga_reseller;

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

    public String getKd_kategori() {
        return kd_kategori;
    }

    public void setKd_kategori(String kd_kategori) {
        this.kd_kategori = kd_kategori;
    }

    public int getHarga_beli() {
        return harga_beli;
    }

    public void setHarga_beli(int harga_beli) {
        this.harga_beli = harga_beli;
    }

    public int getHarga_jual() {
        return harga_jual;
    }

    public void setHarga_jual(int harga_jual) {
        this.harga_jual = harga_jual;
    }

    public String getSatuan() {
        return satuan;
    }

    public void setSatuan(String satuan) {
        this.satuan = satuan;
    }

    public int getStok() {
        return stok;
    }

    public void setStok(int stok) {
        this.stok = stok;
    }

    public int getKonversi() {
        return konversi;
    }

    public void setKonversi(int konversi) {
        this.konversi = konversi;
    }

    public int getHj() {
        return hj;
    }

    public void setHj(int hj) {
        this.hj = hj;
    }

    public int getHarga_member() {
        return harga_member;
    }

    public void setHarga_member(int harga_member) {
        this.harga_member = harga_member;
    }

    public int getDiskon_member() {
        return diskon_member;
    }

    public void setDiskon_member(int diskon_member) {
        this.diskon_member = diskon_member;
    }

    public String getKd_supplier() {
        return kd_supplier;
    }

    public void setKd_supplier(String kd_supplier) {
        this.kd_supplier = kd_supplier;
    }

    public int getDiskon() {
        return diskon;
    }

    public void setDiskon(int diskon) {
        this.diskon = diskon;
    }

    public int getHarga_freelance() {
        return harga_freelance;
    }

    public void setHarga_freelance(int harga_freelance) {
        this.harga_freelance = harga_freelance;
    }

    public int getHarga_karton_freelance() {
        return harga_karton_freelance;
    }

    public void setHarga_karton_freelance(int harga_karton_freelance) {
        this.harga_karton_freelance = harga_karton_freelance;
    }
}
