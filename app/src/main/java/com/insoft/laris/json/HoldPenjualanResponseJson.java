package com.insoft.laris.json;

import com.insoft.laris.model.Hold;

import java.util.ArrayList;
import java.util.List;

public class HoldPenjualanResponseJson {
    private String resultcode;
    private List<Hold> data = new ArrayList<>();

    public String getResultcode() {
        return resultcode;
    }

    public void setResultcode(String resultcode) {
        this.resultcode = resultcode;
    }

    public List<Hold> getData() {
        return data;
    }

    public void setData(List<Hold> data) {
        this.data = data;
    }
}
