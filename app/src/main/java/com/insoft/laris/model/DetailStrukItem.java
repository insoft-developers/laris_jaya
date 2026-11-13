
package com.insoft.laris.model;

import com.google.gson.annotations.SerializedName;

public class DetailStrukItem {

    @SerializedName("id")
    private String id;

    @SerializedName("nota")
    private String nota;

    @SerializedName("kd_barang")
    private String kdBarang;

    @SerializedName("nm_barang")
    private String nmBarang;

    @SerializedName("satuan")
    private String satuan;

    @SerializedName("jumlah")
    private long jumlah;

    @SerializedName("harga")
    private long harga;

    @SerializedName("total")
    private long total;

    @SerializedName("subtotal")
    private long subtotal;

    @SerializedName("disk")
    private long disk;

    @SerializedName("price_type")
    private String priceType;

    public String getId() { return id; }
    public String getNota() { return nota; }
    public String getKdBarang() { return kdBarang; }
    public String getNm_barang() { return nmBarang; }
    public String getSatuan() { return satuan; }
    public long getJumlah() { return jumlah; }
    public long getHarga() { return harga; }
    public long getTotal() { return total; }
    public long getSubtotal() { return subtotal; }
    public long getDisk() { return disk; }
    public String getPriceType() { return priceType; }
}
