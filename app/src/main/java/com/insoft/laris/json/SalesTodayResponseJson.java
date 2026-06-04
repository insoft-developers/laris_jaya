package com.insoft.laris.json;


import com.insoft.laris.model.SalesToday;

import java.util.ArrayList;
import java.util.List;

public class SalesTodayResponseJson {
    private String resultcode;
    private List<SalesToday> data = new ArrayList<>();
    private int total;

    public String getResultcode() {
        return resultcode;
    }

    public void setResultcode(String resultcode) {
        this.resultcode = resultcode;
    }

    public List<SalesToday> getData() {
        return data;
    }

    public void setData(List<SalesToday> data) {
        this.data = data;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
