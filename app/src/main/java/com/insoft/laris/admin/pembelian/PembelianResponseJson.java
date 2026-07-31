package com.insoft.laris.admin.pembelian;

import java.util.List;

public class PembelianResponseJson {
    private String resultcode;
    private List<Pembelian> data;

    public String getResultcode() {
        return resultcode;
    }

    public List<Pembelian> getData() {
        return data;
    }
}
