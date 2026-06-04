package com.insoft.laris.model;

public class SalesMonth {
    private int total_penjualan;
    private String tanggal_penjualan;
    private int belanja;

    public int getTotal_penjualan() {
        return total_penjualan;
    }

    public void setTotal_penjualan(int total_penjualan) {
        this.total_penjualan = total_penjualan;
    }

    public String getTanggal_penjualan() {
        return tanggal_penjualan;
    }

    public void setTanggal_penjualan(String tanggal_penjualan) {
        this.tanggal_penjualan = tanggal_penjualan;
    }

    public int getBelanja() {
        return belanja;
    }

    public void setBelanja(int belanja) {
        this.belanja = belanja;
    }
}
