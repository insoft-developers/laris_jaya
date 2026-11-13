package com.insoft.laris.json;



public class RiwayatStrukRequestJson {

    private String iduser;
    private int idcabang;
    private String search;
    private int limit = 25;

    public void setIduser(String iduser) {
        this.iduser = iduser;
    }

    public void setIdcabang(int idcabang) {
        this.idcabang = idcabang;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}
