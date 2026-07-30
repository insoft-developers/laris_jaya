package com.insoft.laris.admin.piutang;

public class PembayaranSimpanRequestJson {
    private String nota;
    private String pelanggan;
    private int nilai_nota;
    private int pembayaran;
    private String keterangan;
    private String kd_user;

    public void setKd_user(String kd_user) {
        this.kd_user = kd_user;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }

    public void setPelanggan(String pelanggan) {
        this.pelanggan = pelanggan;
    }

    public void setNilai_nota(int nilai_nota) {
        this.nilai_nota = nilai_nota;
    }

    public void setPembayaran(int pembayaran) {
        this.pembayaran = pembayaran;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }
}
