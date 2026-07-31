package com.insoft.laris.admin.pembelian;


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

public class PembelianItem extends RecyclerView.Adapter<PembelianItem.ViewHolder> {

    private Context context;
    private List<Pembelian> pembelian;
    private PembelianInterface pembelianInterface;




    public PembelianItem(Context context, List<Pembelian> pembelian, PembelianInterface pembelianInterface) {
        this.context = context;
        this.pembelian = pembelian;
        this.pembelianInterface = pembelianInterface;


    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_pembelian, parent, false);
        return new ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);

        holder.tvNota.setText(pembelian.get(position).getNota());
        holder.tvTanggal.setText(pembelian.get(position).getTanggal());
        holder.tvSupplier.setText(pembelian.get(position).getNm_supplier());
        holder.tvPengguna.setText(pembelian.get(position).getNama());
        holder.tvKeterangan.setText(pembelian.get(position).getKeterangan());
        holder.tvSubtotal.setText(formatRupiah.format(pembelian.get(position).getSubtotal()));
        holder.tvTotalDiskon.setText(formatRupiah.format(pembelian.get(position).getTotal_discount()));
        holder.tvTotalPembelian.setText(formatRupiah.format(pembelian.get(position).getTotal_pembelian()));
        holder.btnDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pembelianInterface.detail(position);
            }
        });


    }

    @Override
    public int getItemCount() {
        return pembelian.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private MaterialCardView cardPembelian;

        private View viewStatusPembelian;

        private TextView tvNota;
        private TextView tvTanggal;
        private TextView tvStatus;
        private TextView tvSupplier;
        private TextView tvPengguna;
        private TextView tvSubtotal;
        private TextView tvTotalDiskon;
        private TextView tvTotalPembelian;
        private TextView tvKeterangan;

        private LinearLayout layoutDiskon;
        private LinearLayout layoutKeterangan;

        private MaterialButton btnDetail;
        private MaterialButton btnHapus;

        public ViewHolder(View itemView) {
            super(itemView);

            cardPembelian =
                    itemView.findViewById(R.id.cardPembelian);

            viewStatusPembelian =
                    itemView.findViewById(R.id.viewStatusPembelian);

            tvNota =
                    itemView.findViewById(R.id.tvNota);

            tvTanggal =
                    itemView.findViewById(R.id.tvTanggal);

            tvStatus =
                    itemView.findViewById(R.id.tvStatus);

            tvSupplier =
                    itemView.findViewById(R.id.tvSupplier);

            tvPengguna =
                    itemView.findViewById(R.id.tvPengguna);

            tvSubtotal =
                    itemView.findViewById(R.id.tvSubtotal);

            layoutDiskon =
                    itemView.findViewById(R.id.layoutDiskon);

            tvTotalDiskon =
                    itemView.findViewById(R.id.tvTotalDiskon);

            tvTotalPembelian =
                    itemView.findViewById(R.id.tvTotalPembelian);

            layoutKeterangan =
                    itemView.findViewById(R.id.layoutKeterangan);

            tvKeterangan =
                    itemView.findViewById(R.id.tvKeterangan);

            btnDetail =
                    itemView.findViewById(R.id.btnDetail);

            btnHapus =
                    itemView.findViewById(R.id.btnHapus);

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