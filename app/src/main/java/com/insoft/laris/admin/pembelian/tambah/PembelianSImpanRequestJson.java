package com.insoft.laris.admin.pembelian.tambah;

import com.insoft.laris.admin.pembelian.Pembelian;
import com.insoft.laris.admin.pembelian.PembelianDetail;

import java.util.List;

public class PembelianSImpanRequestJson {
    private String kd_supplier;
    private String keterangan;
    private String tanggal;
    private String kd_user;
    private int status;

    private int subtotal;
    private int total_discount;
    private int total_pembelian;

    private List<PembelianDetail>items;

    public void setKd_supplier(String kd_supplier) {
        this.kd_supplier = kd_supplier;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public void setKd_user(String kd_user) {
        this.kd_user = kd_user;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setSubtotal(int subtotal) {
        this.subtotal = subtotal;
    }

    public void setTotal_discount(int total_discount) {
        this.total_discount = total_discount;
    }

    public void setTotal_pembelian(int total_pembelian) {
        this.total_pembelian = total_pembelian;
    }

    public void setItems(List<PembelianDetail> items) {
        this.items = items;
    }
}
