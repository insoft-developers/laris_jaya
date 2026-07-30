package com.insoft.laris.admin.supplier;

import java.util.List;

public class SupplierResponseJson {
    private String resultcode;
    private List<Supplier> data;

    public String getResultcode() {
        return resultcode;
    }

    public List<Supplier> getData() {
        return data;
    }
}
