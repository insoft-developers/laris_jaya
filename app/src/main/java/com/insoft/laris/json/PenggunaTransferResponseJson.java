package com.insoft.laris.json;

import com.insoft.laris.admin.pengguna.PenggunaModel;
import com.insoft.laris.model.Pelanggan;

import java.util.ArrayList;
import java.util.List;

public class PenggunaTransferResponseJson {

    private String resultcode;
    private List<PenggunaModel> data = new ArrayList<>();

    public String getResultcode() {
        return resultcode;
    }

    public List<PenggunaModel> getData() {
        return data;
    }
}
