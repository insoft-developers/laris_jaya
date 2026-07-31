package com.insoft.laris.admin.pembelian;

import java.util.List;

public class Pembelian {
    private String nota;
    private String kd_supplier;

    private String nm_supplier;
    private String keterangan;
    private String tanggal;
    private String kd_user;

    private String nama;
    private String status;

    private int subtotal;
    private int total_discount;
    private int total_pembelian;

    private List<PembelianDetail> items;




    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }

    public String getKd_supplier() {
        return kd_supplier;
    }

    public void setKd_supplier(String kd_supplier) {
        this.kd_supplier = kd_supplier;
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

    public String getKd_user() {
        return kd_user;
    }

    public void setKd_user(String kd_user) {
        this.kd_user = kd_user;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }



    public int getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(int subtotal) {
        this.subtotal = subtotal;
    }

    public int getTotal_discount() {
        return total_discount;
    }

    public void setTotal_discount(int total_discount) {
        this.total_discount = total_discount;
    }

    public int getTotal_pembelian() {
        return total_pembelian;
    }

    public void setTotal_pembelian(int total_pembelian) {
        this.total_pembelian = total_pembelian;
    }

    public String getNm_supplier() {
        return nm_supplier;
    }

    public void setNm_supplier(String nm_supplier) {
        this.nm_supplier = nm_supplier;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public List<PembelianDetail> getItems() {
        return items;
    }

    public void setItems(List<PembelianDetail> items) {
        this.items = items;
    }
}
