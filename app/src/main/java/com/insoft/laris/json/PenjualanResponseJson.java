package com.insoft.laris.json;

import com.insoft.laris.model.Penjualan;

import java.util.ArrayList;
import java.util.List;

public class PenjualanResponseJson {
    private String resultcode;
    private List<Penjualan> data = new ArrayList<>();
    private int total;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getResultcode() {
        return resultcode;
    }

    public void setResultcode(String resultcode) {
        this.resultcode = resultcode;
    }

    public List<Penjualan> getData() {
        return data;
    }

    public void setData(List<Penjualan> data) {
        this.data = data;
    }
}
