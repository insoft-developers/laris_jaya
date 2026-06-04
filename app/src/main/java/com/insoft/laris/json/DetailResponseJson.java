package com.insoft.laris.json;



import com.insoft.laris.model.Header;
import com.insoft.laris.model.Item;

import java.util.ArrayList;
import java.util.List;

public class DetailResponseJson {
    private String resultcode;
    private List<Header> data = new ArrayList<>();
    private List<Item> item = new ArrayList<>();

    public String getResultcode() {
        return resultcode;
    }

    public void setResultcode(String resultcode) {
        this.resultcode = resultcode;
    }

    public List<Header> getData() {
        return data;
    }

    public void setData(List<Header> data) {
        this.data = data;
    }

    public List<Item> getItem() {
        return item;
    }

    public void setItem(List<Item> item) {
        this.item = item;
    }
}
