package com.insoft.laris.model;

public class Hold {
    private int id;
    private int total;
    private String customer_code;
    private String customer_name;
    private String customer_address;
    private int id_hold;
    private String tanggal_transaksi;
    private int total_penjualan;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getCustomer_code() {
        return customer_code;
    }

    public void setCustomer_code(String customer_code) {
        this.customer_code = customer_code;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public String getCustomer_address() {
        return customer_address;
    }

    public void setCustomer_address(String customer_address) {
        this.customer_address = customer_address;
    }

    public int getId_hold() {
        return id_hold;
    }

    public void setId_hold(int id_hold) {
        this.id_hold = id_hold;
    }

    public String getTanggal_transaksi() {
        return tanggal_transaksi;
    }

    public void setTanggal_transaksi(String tanggal_transaksi) {
        this.tanggal_transaksi = tanggal_transaksi;
    }

    public int getTotal_penjualan() {
        return total_penjualan;
    }

    public void setTotal_penjualan(int total_penjualan) {
        this.total_penjualan = total_penjualan;
    }
}
