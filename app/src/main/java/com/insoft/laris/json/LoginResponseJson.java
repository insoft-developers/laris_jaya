package com.insoft.laris.json;

import com.insoft.laris.model.User;

import java.util.ArrayList;
import java.util.List;

public class LoginResponseJson {
    private String resultcode;
    private List<User> data = new ArrayList<>();

    public String getResultcode() {
        return resultcode;
    }

    public void setResultcode(String resultcode) {
        this.resultcode = resultcode;
    }

    public List<User> getData() {
        return data;
    }

    public void setData(List<User> data) {
        this.data = data;
    }
}
