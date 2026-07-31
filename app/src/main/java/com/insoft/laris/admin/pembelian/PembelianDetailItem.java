package com.insoft.laris.admin.pembelian;


import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
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
import com.insoft.laris.admin.piutang.Piutang;
import com.insoft.laris.admin.piutang.PiutangInterface;
import com.insoft.laris.admin.supplier.Supplier;
import com.insoft.laris.admin.supplier.SupplierInterface;
import com.insoft.laris.model.Pelanggan;
import com.insoft.laris.utils.MyDatabaseHelper;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PembelianDetailItem extends RecyclerView.Adapter<PembelianDetailItem.ViewHolder> {

    private Context context;
    private List<PembelianDetail> details;




    public PembelianDetailItem(Context context, List<PembelianDetail> details) {
        this.context = context;
        this.details = details;


    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_detail_pembelian, parent, false);
        return new ViewHolder(view);
    }




    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        holder.tvNamaProduk.setText(details.get(position).getNm_barang());
        holder.tvKodeProduk.setText(details.get(position).getKd_barang());
        holder.tvJumlahHarga.setText(details.get(position).getJumlah() +" X "+formatRupiah.format(details.get(position).getHarga()));
        holder.tvSubtotalProduk.setText(formatRupiah.format(details.get(position).getSubtotal()));
        holder.tvDiskonProduk.setText(formatRupiah.format(details.get(position).getDiskon()));
        holder.tvTotalProduk.setText(formatRupiah.format(details.get(position).getTotal()));
        int nomor = position+1;
        holder.tvNomorProduk.setText(String.valueOf(nomor));


    }

    @Override
    public int getItemCount() {
        return details.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvNomorProduk;
        private TextView tvNamaProduk;
        private TextView tvKodeProduk;
        private TextView tvJumlahHarga;
        private TextView tvSubtotalProduk;
        private TextView tvDiskonProduk;
        private TextView tvTotalProduk;

        private LinearLayout layoutDiskonProduk;
        public ViewHolder(View itemView) {
            super(itemView);
            tvNomorProduk =
                    itemView.findViewById(R.id.tvNomorProduk);

            tvNamaProduk =
                    itemView.findViewById(R.id.tvNamaProduk);

            tvKodeProduk =
                    itemView.findViewById(R.id.tvKodeProduk);

            tvJumlahHarga =
                    itemView.findViewById(R.id.tvJumlahHarga);

            tvSubtotalProduk =
                    itemView.findViewById(R.id.tvSubtotalProduk);

            layoutDiskonProduk =
                    itemView.findViewById(R.id.layoutDiskonProduk);

            tvDiskonProduk =
                    itemView.findViewById(R.id.tvDiskonProduk);

            tvTotalProduk =
                    itemView.findViewById(R.id.tvTotalProduk);



        }
    }
}