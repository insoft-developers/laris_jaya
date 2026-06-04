package com.insoft.laris.json;

import com.insoft.laris.model.Kategori;

import java.util.ArrayList;

public class KategoriResponseJson {
    private String resultcode;
    private ArrayList<Kategori> data = new ArrayList<>();

    public String getResultcode() {
        return resultcode;
    }

    public void setResultcode(String resultcode) {
        this.resultcode = resultcode;
    }

    public ArrayList<Kategori> getData() {
        return data;
    }

    public void setData(ArrayList<Kategori> data) {
        this.data = data;
    }
}
