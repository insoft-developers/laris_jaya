
package com.insoft.laris.json;

public class DetailStrukRequestJson {

    private String nota;
    private String iduser;
    private int idcabang;

    public DetailStrukRequestJson() {
    }

    public void setNota(String nota) {
        this.nota = nota;
    }

    public void setIduser(String iduser) {
        this.iduser = iduser;
    }

    public void setIdcabang(int idcabang) {
        this.idcabang = idcabang;
    }

    public String getNota() {
        return nota;
    }

    public String getIduser() {
        return iduser;
    }

    public int getIdcabang() {
        return idcabang;
    }
}
