package com.insoft.laris.model;

public class PerInvoice {
    private int id;
    private String nota;
    private String kd_pelanggan;
    private int total_penjualan;
    private String kd_user;
    private String keterangan;
    private String tanggal;
    private String nm_pelanggan;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }

    public String getKd_pelanggan() {
        return kd_pelanggan;
    }

    public void setKd_pelanggan(String kd_pelanggan) {
        this.kd_pelanggan = kd_pelanggan;
    }

    public int getTotal_penjualan() {
        return total_penjualan;
    }

    public void setTotal_penjualan(int total_penjualan) {
        this.total_penjualan = total_penjualan;
    }

    public String getKd_user() {
        return kd_user;
    }

    public void setKd_user(String kd_user) {
        this.kd_user = kd_user;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getNm_pelanggan() {
        return nm_pelanggan;
    }

    public void setNm_pelanggan(String nm_pelanggan) {
        this.nm_pelanggan = nm_pelanggan;
    }
}
