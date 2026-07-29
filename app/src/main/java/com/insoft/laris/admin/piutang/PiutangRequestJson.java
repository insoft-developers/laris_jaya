package com.insoft.laris.admin.piutang;

public class PiutangRequestJson {
    private String kata_cari;

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    private String filter;

    public String getKata_cari() {
        return kata_cari;
    }

    public void setKata_cari(String kata_cari) {
        this.kata_cari = kata_cari;
    }


}
