package com.insoft.laris.admin.pembayaran;

public class Pembayaran {
    private int id;
    private String no_pembayaran;
    private String nota;
    private String pelanggan;
    private String nm_pelanggan;
    private int nilai_nota;
    private int pembayaran;
    private  int sisa;
    private String tanggal;
    private String keterangan;
    private String kd_user;

    private String nama;


    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getKd_user() {
        return kd_user;
    }

    public void setKd_user(String kd_user) {
        this.kd_user = kd_user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNo_pembayaran() {
        return no_pembayaran;
    }

    public void setNo_pembayaran(String no_pembayaran) {
        this.no_pembayaran = no_pembayaran;
    }

    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }

    public String getPelanggan() {
        return pelanggan;
    }

    public void setPelanggan(String pelanggan) {
        this.pelanggan = pelanggan;
    }

    public String getNm_pelanggan() {
        return nm_pelanggan;
    }

    public void setNm_pelanggan(String nm_pelanggan) {
        this.nm_pelanggan = nm_pelanggan;
    }

    public int getNilai_nota() {
        return nilai_nota;
    }

    public void setNilai_nota(int nilai_nota) {
        this.nilai_nota = nilai_nota;
    }

    public int getPembayaran() {
        return pembayaran;
    }

    public void setPembayaran(int pembayaran) {
        this.pembayaran = pembayaran;
    }

    public int getSisa() {
        return sisa;
    }

    public void setSisa(int sisa) {
        this.sisa = sisa;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }
}
