package com.insoft.laris.model;

public class RingkasanTransaksi {

    private final long subtotal;
    private final long diskon;
    private final long total;

    public RingkasanTransaksi(long subtotal, long diskon, long total) {
        this.subtotal = subtotal;
        this.diskon = diskon;
        this.total = total;
    }

    public long getSubtotal() {
        return subtotal;
    }

    public long getDiskon() {
        return diskon;
    }

    public long getTotal() {
        return total;
    }
}