package com.insoft.laris.model;

public class User {
    private String kd_pengguna;
    private String nm_pengguna;
    private String nama;
    private String alamat;
    private String telepon;
    private String level;

    public String getKd_pengguna() {
        return kd_pengguna;
    }

    public void setKd_pengguna(String kd_pengguna) {
        this.kd_pengguna = kd_pengguna;
    }

    public String getNm_pengguna() {
        return nm_pengguna;
    }

    public void setNm_pengguna(String nm_pengguna) {
        this.nm_pengguna = nm_pengguna;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getTelepon() {
        return telepon;
    }

    public void setTelepon(String telepon) {
        this.telepon = telepon;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
