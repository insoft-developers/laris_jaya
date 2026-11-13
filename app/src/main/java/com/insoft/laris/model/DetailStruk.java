
package com.insoft.laris.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class DetailStruk {

    @SerializedName("id")
    private String id;

    @SerializedName("nota")
    private String nota;

    @SerializedName("tanggal")
    private String tanggal;

    @SerializedName("kd_pelanggan")
    private String kdPelanggan;

    @SerializedName("nm_pelanggan")
    private String nmPelanggan;

    @SerializedName("telepon")
    private String telepon;

    @SerializedName("kd_user")
    private String kdUser;

    @SerializedName("nm_pengguna")
    private String nmPengguna;

    @SerializedName("subtotal")
    private long subtotal;

    @SerializedName("total_discount")
    private long totalDiscount;

    @SerializedName("belanja")
    private long belanja;

    @SerializedName("bayar")
    private long bayar;

    @SerializedName("kembali")
    private long kembali;

    @SerializedName("items")
    private List<DetailStrukItem> items;

    public String getId() { return id; }
    public String getNota() { return nota; }
    public String getTanggal() { return tanggal; }
    public String getKdPelanggan() { return kdPelanggan; }
    public String getNm_pelanggan() { return nmPelanggan; }
    public String getTelepon() { return telepon; }
    public String getKdUser() { return kdUser; }
    public String getNmPengguna() { return nmPengguna; }

    public long getSubtotal() { return subtotal; }
    public long getTotal_discount() { return totalDiscount; }
    public long getBelanja() { return belanja; }
    public long getBayar() { return bayar; }
    public long getKembali() { return kembali; }

    public List<DetailStrukItem> getItems() {
        return items;
    }
}
