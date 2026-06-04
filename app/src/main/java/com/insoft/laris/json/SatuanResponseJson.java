package com.insoft.laris.json;

import com.insoft.laris.model.Satuan;

import java.util.ArrayList;

public class SatuanResponseJson {
    private String resultcode;
    private ArrayList<Satuan> data = new ArrayList<>();

    public String getResultcode() {
        return resultcode;
    }

    public void setResultcode(String resultcode) {
        this.resultcode = resultcode;
    }

    public ArrayList<Satuan> getData() {
        return data;
    }

    public void setData(ArrayList<Satuan> data) {
        this.data = data;
    }
}
