package com.insoft.laris.model;

public class SalesTodayItem {
    private int id;
    private String nm_barang;
    private String satuan;
    private int jumlahpenjualan;
    private int nilaipenjualan;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getJumlahpenjualan() {
        return jumlahpenjualan;
    }

    public void setJumlahpenjualan(int jumlahpenjualan) {
        this.jumlahpenjualan = jumlahpenjualan;
    }

    public int getNilaipenjualan() {
        return nilaipenjualan;
    }

    public void setNilaipenjualan(int nilaipenjualan) {
        this.nilaipenjualan = nilaipenjualan;
    }
}
