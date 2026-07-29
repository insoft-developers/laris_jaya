package com.insoft.laris.admin.piutang;

import java.util.List;

public class PiutangResponseJson {
    private String resultcode;
    private List<Piutang> data;

    public String getResultcode() {
        return resultcode;
    }

    public void setResultcode(String resultcode) {
        this.resultcode = resultcode;
    }

    public List<Piutang> getData() {
        return data;
    }

    public void setData(List<Piutang> data) {
        this.data = data;
    }
}
