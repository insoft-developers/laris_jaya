package com.insoft.laris.json;



import com.google.gson.annotations.SerializedName;
import com.insoft.laris.model.DetailStruk;

public class DetailStrukResponseJson {

    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private DetailStruk data;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public DetailStruk getData() {
        return data;
    }
}
