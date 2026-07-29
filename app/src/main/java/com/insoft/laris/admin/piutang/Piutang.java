package com.insoft.laris.admin.piutang;

public class Piutang {
    private int id;
    private String nota;
    private String kd_pelanggan;

    private String nm_pelanggan;
    private String keterangan;
    private String tanggal;
    private int belanja;
    private int bayar;
    private int sisa;
    private String kd_user;
    private int tempo_hari;
    private String jatuh_tempo;

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

    public String getNm_pelanggan() {
        return nm_pelanggan;
    }

    public void setNm_pelanggan(String nm_pelanggan) {
        this.nm_pelanggan = nm_pelanggan;
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

    public int getSisa() {
        return sisa;
    }

    public void setSisa(int sisa) {
        this.sisa = sisa;
    }

    public String getKd_user() {
        return kd_user;
    }

    public void setKd_user(String kd_user) {
        this.kd_user = kd_user;
    }

    public int getTempo_hari() {
        return tempo_hari;
    }

    public void setTempo_hari(int tempo_hari) {
        this.tempo_hari = tempo_hari;
    }

    public String getJatuh_tempo() {
        return jatuh_tempo;
    }

    public void setJatuh_tempo(String jatuh_tempo) {
        this.jatuh_tempo = jatuh_tempo;
    }
}
