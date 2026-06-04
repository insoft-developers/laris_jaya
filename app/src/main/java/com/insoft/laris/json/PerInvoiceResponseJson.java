package com.insoft.laris.json;



import com.insoft.laris.model.PerInvoice;

import java.util.ArrayList;
import java.util.List;

public class PerInvoiceResponseJson {
    private String resultcode;
    private List<PerInvoice> data = new ArrayList<>();
    private int total;

    public String getResultcode() {
        return resultcode;
    }

    public void setResultcode(String resultcode) {
        this.resultcode = resultcode;
    }

    public List<PerInvoice> getData() {
        return data;
    }

    public void setData(List<PerInvoice> data) {
        this.data = data;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
