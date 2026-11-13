package com.insoft.laris.json;

import com.insoft.laris.model.RiwayatStruk;

import java.util.List;

public class RiwayatStrukResponseJson {

    private boolean success;
    private String message;
    private List<RiwayatStruk> data;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public List<RiwayatStruk> getData() {
        return data;
    }
}
