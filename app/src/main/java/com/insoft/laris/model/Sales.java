package com.insoft.laris.model;

import java.util.List;

public class Sales {
    public String nota;
    public String kd_pelanggan;
    public String keterangan;
    public String tanggal;
    public int belanja;
    public int bayar;
    public int donasi;
    public int kembali;
    public String kd_user;
    public int depo;
    public int bank_deposit;
    public List<SalesItem> item;

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

    public int getBelanja() {
        return belanja;
    }

    public void setBelanja(int belanja) {
        this.belanja = belanja;
    }

    public int getBayar() {
        return bayar;
    }

    public void setBayar(int bayar) {
        this.bayar = bayar;
    }

    public int getDonasi() {
        return donasi;
    }

    public void setDonasi(int donasi) {
        this.donasi = donasi;
    }

    public int getKembali() {
        return kembali;
    }

    public void setKembali(int kembali) {
        this.kembali = kembali;
    }

    public String getKd_user() {
        return kd_user;
    }

    public void setKd_user(String kd_user) {
        this.kd_user = kd_user;
    }

    public int getDepo() {
        return depo;
    }

    public void setDepo(int depo) {
        this.depo = depo;
    }

    public int getBank_deposit() {
        return bank_deposit;
    }

    public void setBank_deposit(int bank_deposit) {
        this.bank_deposit = bank_deposit;
    }

    public List<SalesItem> getItem() {
        return item;
    }

    public void setItem(List<SalesItem> item) {
        this.item = item;
    }
}



