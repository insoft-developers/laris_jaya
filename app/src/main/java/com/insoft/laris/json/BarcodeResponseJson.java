package com.insoft.laris.json;

import com.insoft.laris.model.Produk;

import java.util.ArrayList;
import java.util.List;

public class BarcodeResponseJson {
    private String resultcode;
    private String pesan;
    private List<Produk> data = new ArrayList<>();

    public String getPesan() {
        return pesan;
    }

    public void setPesan(String pesan) {
        this.pesan = pesan;
    }

    public String getResultcode() {
        return resultcode;
    }

    public void setResultcode(String resultcode) {
        this.resultcode = resultcode;
    }

    public List<Produk> getData() {
        return data;
    }

    public void setData(List<Produk> data) {
        this.data = data;
    }
}
