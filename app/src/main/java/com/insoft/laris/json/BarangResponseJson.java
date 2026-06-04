package com.insoft.laris.json;

import com.insoft.laris.model.Produk;

import java.util.ArrayList;
import java.util.List;

public class BarangResponseJson {
    private String resultcode;
    private List<Produk> data = new ArrayList<>();

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
