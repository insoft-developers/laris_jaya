package com.insoft.laris.utils;

import com.insoft.laris.admin.pembayaran.PembayaranHapusRequestJson;
import com.insoft.laris.admin.pembayaran.PembayaranHapusResponseJson;
import com.insoft.laris.admin.pembayaran.PembayaranRequestJson;
import com.insoft.laris.admin.pembayaran.PembayaranResponseJson;
import com.insoft.laris.admin.pengguna.PenggunaRequestJson;
import com.insoft.laris.admin.pengguna.PenggunaResponseJson;
import com.insoft.laris.admin.piutang.PembayaranSimpanRequestJson;
import com.insoft.laris.admin.piutang.PembayaranSimpanResponseJson;
import com.insoft.laris.admin.piutang.PiutangRequestJson;
import com.insoft.laris.admin.piutang.PiutangResponseJson;
import com.insoft.laris.json.BarangResponseJson;
import com.insoft.laris.json.BarcodeCekRequestJson;
import com.insoft.laris.json.BarcodeCekResponseJson;
import com.insoft.laris.json.BarcodeRequestJson;
import com.insoft.laris.json.BarcodeResponseJson;
import com.insoft.laris.json.CustomerRequestJson;
import com.insoft.laris.json.CustomerResponseJson;
import com.insoft.laris.json.DetailRequestJson;
import com.insoft.laris.json.DetailResponseJson;
import com.insoft.laris.json.GeneralResponseJson;
import com.insoft.laris.json.HapusItemRequestJson;
import com.insoft.laris.json.HapusItemResponseJson;
import com.insoft.laris.json.HapusProdukRequestJson;
import com.insoft.laris.json.HapusProdukResponseJson;
import com.insoft.laris.json.HoldPenjualanResponseJson;
import com.insoft.laris.json.KategoriResponseJson;
import com.insoft.laris.json.LoginRequestJson;
import com.insoft.laris.json.LoginResponseJson;
import com.insoft.laris.json.PelangganRequestJson;
import com.insoft.laris.json.PelangganResponseJson;
import com.insoft.laris.json.PenjualanRequestJson;
import com.insoft.laris.json.PenjualanResponseJson;
import com.insoft.laris.json.PerInvoiceRequestJson;
import com.insoft.laris.json.PerInvoiceResponseJson;
import com.insoft.laris.json.ProdukRequestJson;
import com.insoft.laris.json.ProdukResponseJson;
import com.insoft.laris.json.SalesMonthResponseJson;
import com.insoft.laris.json.SalesRequestJson;
import com.insoft.laris.json.SalesResponseJson;
import com.insoft.laris.json.SalesTanggalRequestJson;
import com.insoft.laris.json.SalesTodayItemResponseJson;
import com.insoft.laris.json.SalesTodayRequestJson;
import com.insoft.laris.json.SalesTodayResponseJson;
import com.insoft.laris.json.SatuanResponseJson;
import com.insoft.laris.json.SimpanProdukRequestJson;
import com.insoft.laris.json.SimpanProdukResponseJson;
import com.insoft.laris.json.SubmitRequestJson;
import com.insoft.laris.json.SubmitResponseJson;
import com.insoft.laris.json.TransferPenjualanRequestJson;
import com.insoft.laris.json.TransferPenjualanResponseJson;
import com.insoft.laris.json.UpadateItemResponseJson;
import com.insoft.laris.json.UpdateItemRequestJson;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface RegisterAPI {
    @Headers("Content-Type: application/json")

    @POST("submit_penjualan")
    Call<SubmitResponseJson> submit_penjualan(@Body SubmitRequestJson param);

    @POST("login")
    Call<LoginResponseJson> login(@Body LoginRequestJson param);

    @POST("get_produk")
    Call<BarcodeResponseJson> get_produk(@Body BarcodeRequestJson param);

    @POST("get_produk_by_kode")
    Call<ProdukResponseJson> get_produk_by_kode(@Body ProdukRequestJson param);

    @POST("daftar_barang")
    Call<BarangResponseJson> daftar_barang(@Body CustomerRequestJson param);

    @POST("daftar_barang")
    Call<BarangResponseJson> product_list(@Body CustomerRequestJson param);

    @POST("daftar_pelanggan")
    Call<CustomerResponseJson> get_customer(@Body CustomerRequestJson param);


    @POST("tambah_pelanggan")
    Call<PelangganResponseJson> tambah_pelanggan (@Body PelangganRequestJson param);

    @POST("hapus_pelanggan")
    Call<PelangganResponseJson> hapus_pelanggan (@Body PelangganRequestJson param);

    @POST("update_pelanggan")
    Call<PelangganResponseJson> update_pelanggan (@Body PelangganRequestJson param);

    @GET("hold_penjualan")
    Call<HoldPenjualanResponseJson> hold_penjualan();

    @POST("hold_by_id")
    Call<PenjualanResponseJson> penjualan(@Body PenjualanRequestJson param);

    @POST("transfer_penjualan")
    Call<TransferPenjualanResponseJson> transfer(@Body TransferPenjualanRequestJson param);

    @POST("update_item")
    Call<UpadateItemResponseJson> update_item(@Body UpdateItemRequestJson param);

    @POST("hapus_item")
    Call<HapusItemResponseJson> hapus_item(@Body HapusItemRequestJson param);

    @GET("list_kategori")
    Call<KategoriResponseJson> list_kategori();

    @GET("list_satuan")
    Call<SatuanResponseJson> list_satuan();

    @POST("simpan_produk")
    Call<SimpanProdukResponseJson> simpan_produk(@Body SimpanProdukRequestJson param);

    @POST("update_produk")
    Call<SimpanProdukResponseJson> update_produk(@Body SimpanProdukRequestJson param);

    @POST("hapus_produk")
    Call<HapusProdukResponseJson> hapus_produk(@Body HapusProdukRequestJson param);

    @POST("cek_barcode")
    Call<BarcodeCekResponseJson> cek_barcode(@Body BarcodeCekRequestJson param);

    @POST("kirim_penjualan_ke_server")
    Call<SalesResponseJson> kirim_penjualan_ke_server(@Body SalesRequestJson param);

    @POST("salestoday")
    Call<SalesTodayResponseJson> salestoday(@Body SalesTodayRequestJson param);

    @POST("salestodayitem")
    Call<SalesTodayItemResponseJson> salestodayitem(@Body SalesTodayRequestJson param);

    @POST("detailinvoice")
    Call<DetailResponseJson> detailinvoice(@Body DetailRequestJson param);

    @POST("salestanggal")
    Call<SalesMonthResponseJson> salestanggal(@Body SalesTanggalRequestJson param);

    @POST("salestanggalitem")
    Call<SalesTodayItemResponseJson> salestanggalitem(@Body SalesTanggalRequestJson param);

    @POST("perinvoice")
    Call<PerInvoiceResponseJson> perinvoice(@Body PerInvoiceRequestJson param);

    @POST("daftar_pengguna")
    Call<PenggunaResponseJson> get_daftar_pengguna(@Body PenggunaRequestJson param);

    @POST("tambah_pengguna")
    Call<GeneralResponseJson> tambahPengguna(@Body PenggunaRequestJson param);

    @POST("update_pengguna")
    Call<GeneralResponseJson> updatePengguna(@Body PenggunaRequestJson param);

    @POST("delete_pengguna")
    Call<GeneralResponseJson> deletePengguna(@Body PenggunaRequestJson param);

    @POST("piutang_list")
    Call<PiutangResponseJson> piutangList(@Body PiutangRequestJson param);

    @POST("pembayaran_list")
    Call<PembayaranResponseJson> pembayaranList(@Body PembayaranRequestJson param);

    @POST("tambah_pembayaran")
    Call<PembayaranSimpanResponseJson> tambah_pembayaran(@Body PembayaranSimpanRequestJson param);

    @POST("hapus_pembayaran")
    Call<PembayaranHapusResponseJson> hapus_pembayaran(@Body PembayaranHapusRequestJson param);


}
