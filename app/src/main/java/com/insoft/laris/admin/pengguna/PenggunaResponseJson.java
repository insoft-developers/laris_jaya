package com.insoft.laris.admin.pengguna;

import java.util.ArrayList;
import java.util.List;

public class PenggunaResponseJson {
    private String resultcode;

    public String getResultcode() {
        return resultcode;
    }

    public void setResultcode(String resultcode) {
        this.resultcode = resultcode;
    }

    public List<PenggunaModel> getData() {
        return data;
    }

    public void setData(List<PenggunaModel> data) {
        this.data = data;
    }

    private List<PenggunaModel> data = new ArrayList<>();
}
