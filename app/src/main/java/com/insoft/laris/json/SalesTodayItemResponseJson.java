package com.insoft.laris.json;


import com.insoft.laris.model.SalesTodayItem;

import java.util.ArrayList;
import java.util.List;

public class SalesTodayItemResponseJson {
    private String resultcode;
    private int total;
    private List<SalesTodayItem> data = new ArrayList<>();

    public String getResultcode() {
        return resultcode;
    }

    public void setResultcode(String resultcode) {
        this.resultcode = resultcode;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<SalesTodayItem> getData() {
        return data;
    }

    public void setData(List<SalesTodayItem> data) {
        this.data = data;
    }
}
