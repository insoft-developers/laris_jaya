package com.insoft.laris.admin.pembelian.tambah;



import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.insoft.laris.Interface.pelangganInterface;
import com.insoft.laris.R;
import com.insoft.laris.admin.pembelian.Pembelian;
import com.insoft.laris.admin.pembelian.PembelianDetail;
import com.insoft.laris.admin.pembelian.PembelianInterface;
import com.insoft.laris.admin.piutang.Piutang;
import com.insoft.laris.admin.piutang.PiutangInterface;
import com.insoft.laris.admin.supplier.Supplier;
import com.insoft.laris.admin.supplier.SupplierInterface;
import com.insoft.laris.model.Pelanggan;
import com.insoft.laris.utils.MyDatabaseHelper;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class PembelianProductItem extends RecyclerView.Adapter<PembelianProductItem.ViewHolder> {

    private Context context;
    ArrayList<HashMap<String, String>> details;


    public PembelianProductItem(Context context, ArrayList<HashMap<String, String>> details) {
        this.context = context;
        this.details = details;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_produk_tambah_pembelian, parent, false);
        return new ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        int nomor = position + 1;
        holder.tvNomorProduk.setText(String.valueOf(nomor));
        holder.tvNamaProduk.setText(details.get(position).get("nm_barang"));
        holder.tvKodeProduk.setText(details.get(position).get("kd_barang"));
        holder.tvSatuanProduk.setText(details.get(position).get("satuan"));
        holder.tvJumlahHarga.setText(String.valueOf(details.get(position).get("jumlah")));
//        holder.tvSubtotalProduk.setText(formatRupiah.format(details.get(position).get("subtotal")));
//        holder.tvDiskonProduk.setText(formatRupiah.format(details.get(position).get("diskon")));
//        holder.tvTotalProduk.setText(formatRupiah.format(details.get(position).get("total")));

        holder.btnEditProduk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        holder.btnHapusProduk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return details.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private MaterialCardView cardProdukPembelian;
        private TextView tvNomorProduk;
        private TextView tvNamaProduk;
        private TextView tvKodeProduk;
        private TextView tvSatuanProduk;
        private TextView tvJumlahHarga;
        private TextView tvSubtotalProduk;
        private LinearLayout layoutDiskonProduk;
        private TextView tvDiskonProduk;
        private TextView tvTotalProduk;
        private MaterialButton btnEditProduk;
        private MaterialButton btnHapusProduk;

        public ViewHolder(View itemView) {
            super(itemView);
            cardProdukPembelian = itemView.findViewById(R.id.cardProdukPembelian);
            tvNomorProduk = itemView.findViewById(R.id.tvNomorProduk);
            tvNamaProduk = itemView.findViewById(R.id.tvNamaProduk);
            tvKodeProduk = itemView.findViewById(R.id.tvKodeProduk);
            tvSatuanProduk = itemView.findViewById(R.id.tvSatuanProduk);
            tvJumlahHarga = itemView.findViewById(R.id.tvJumlahHarga);
            tvSubtotalProduk = itemView.findViewById(R.id.tvSubtotalProduk);
            layoutDiskonProduk = itemView.findViewById(R.id.layoutDiskonProduk);
            tvDiskonProduk = itemView.findViewById(R.id.tvDiskonProduk);
            tvTotalProduk = itemView.findViewById(R.id.tvTotalProduk);
            btnEditProduk = itemView.findViewById(R.id.btnEditProduk);
            btnHapusProduk = itemView.findViewById(R.id.btnHapusProduk);





        }
    }


    private String buatInisial(String nama) {
        if (nama == null || nama.trim().isEmpty()) {
            return "S";
        }

        String[] kata = nama.trim().split("\\s+");

        if (kata.length == 1) {
            return kata[0]
                    .substring(0, 1)
                    .toUpperCase(Locale.ROOT);
        }

        return (
                kata[0].substring(0, 1)
                        + kata[1].substring(0, 1)
        ).toUpperCase(Locale.ROOT);
    }
}