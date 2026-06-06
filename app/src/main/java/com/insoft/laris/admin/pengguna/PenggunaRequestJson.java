package com.insoft.laris.admin.pengguna;

public class PenggunaRequestJson {


    private String kata_cari;
    private String kd_pengguna;
    private String nm_pengguna;
    private String password;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private int id;

    public void setKd_pengguna(String kd_pengguna) {
        this.kd_pengguna = kd_pengguna;
    }

    public void setNm_pengguna(String nm_pengguna) {
        this.nm_pengguna = nm_pengguna;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public void setTelepon(String telepon) {
        this.telepon = telepon;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    private String nama;
    private String alamat;
    private String telepon;
    private String level;

    public String getKata_cari() {
        return kata_cari;
    }

    public void setKata_cari(String kata_cari) {
        this.kata_cari = kata_cari;
    }
}
