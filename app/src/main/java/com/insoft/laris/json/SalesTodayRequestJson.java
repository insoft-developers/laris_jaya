package com.insoft.laris.json;

public class SalesTodayRequestJson {
    private String iduser;
    private int idcabang;

    public int getIdcabang() {
        return idcabang;
    }

    public void setIdcabang(int idcabang) {
        this.idcabang = idcabang;
    }

    public String getIduser() {
        return iduser;
    }

    public void setIduser(String iduser) {
        this.iduser = iduser;
    }
}
