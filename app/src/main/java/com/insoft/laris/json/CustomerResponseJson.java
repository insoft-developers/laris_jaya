package com.insoft.laris.json;

import com.insoft.laris.model.Pelanggan;

import java.util.ArrayList;
import java.util.List;

public class CustomerResponseJson {
    private String resultcode;
    private List<Pelanggan> data = new ArrayList<>();

    public String getResultcode() {
        return resultcode;
    }

    public void setResultcode(String resultcode) {
        this.resultcode = resultcode;
    }

    public List<Pelanggan> getData() {
        return data;
    }

    public void setData(List<Pelanggan> data) {
        this.data = data;
    }
}
