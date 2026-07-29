package com.insoft.laris.admin.pembayaran;

import java.util.List;

public class PembayaranResponseJson {
    private String resultcode;
    private List<Pembayaran> data;

    public String getResultcode() {
        return resultcode;
    }

    public void setResultcode(String resultcode) {
        this.resultcode = resultcode;
    }

    public List<Pembayaran> getData() {
        return data;
    }

    public void setData(List<Pembayaran> data) {
        this.data = data;
    }
}
