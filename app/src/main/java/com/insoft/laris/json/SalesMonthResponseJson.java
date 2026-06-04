package com.insoft.laris.json;



import com.insoft.laris.model.SalesMonth;

import java.util.ArrayList;
import java.util.List;

public class SalesMonthResponseJson {
    private String resultcode;
    private int total;
    private List<SalesMonth> data = new ArrayList<>();

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

    public List<SalesMonth> getData() {
        return data;
    }

    public void setData(List<SalesMonth> data) {
        this.data = data;
    }
}
