package com.insoft.laris.json;

import com.insoft.laris.model.Sales;

import java.util.List;

public class SalesRequestJson {
    private List<Sales> penjualan;

    public List<Sales> getPenjualan() {
        return penjualan;
    }

    public void setPenjualan(List<Sales> penjualan) {
        this.penjualan = penjualan;
    }
}
