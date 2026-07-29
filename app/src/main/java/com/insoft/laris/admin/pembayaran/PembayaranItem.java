package com.insoft.laris.admin.pembayaran;

import android.content.Context;
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
import com.insoft.laris.model.Pelanggan;
import com.insoft.laris.utils.MyDatabaseHelper;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PembayaranItem extends RecyclerView.Adapter<PembayaranItem.ViewHolder> {

    private Context context;
    private List<Pembayaran> pembayaran;



    public PembayaranItem(Context context, List<Pembayaran> pembayaran) {
        this.context = context;
        this.pembayaran = pembayaran;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_pembayaran, parent, false);
        return new ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(PembayaranItem.ViewHolder holder,  final int position) {

        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        holder.tvNamaPelanggan.setText(pembayaran.get(position).getNm_pelanggan());
        holder.tvNoPembayaran.setText(pembayaran.get(position).getNo_pembayaran());
        holder.tvTanggal.setText(formatTanggal(pembayaran.get(position).getTanggal()));
        holder.tvNota.setText(pembayaran.get(position).getNota());
        holder.tvNilaiNota.setText(formatRupiah.format(pembayaran.get(position).getNilai_nota()));
        holder.tvPembayaran.setText(formatRupiah.format(pembayaran.get(position).getPembayaran()));
        holder.tvSisa.setText(formatRupiah.format(pembayaran.get(position).getSisa()));
        holder.tvKeterangan.setText(pembayaran.get(position).getKeterangan());


        holder.rootlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });




    }

    @Override
    public int getItemCount() {
        return pembayaran.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvNamaPelanggan,
                tvNoPembayaran,
                tvTanggal,
                tvNota,
                tvNilaiNota,
                tvPembayaran,
                tvSisa,
                tvKeterangan;

        private MaterialButton btnHapus,
                btnPrint;

        private MaterialCardView rootlayout;

        public ViewHolder(View itemView) {
            super(itemView);
            rootlayout = itemView.findViewById(R.id.rootLayout);

            tvNamaPelanggan = itemView.findViewById(R.id.tvNamaPelanggan);
            tvNoPembayaran = itemView.findViewById(R.id.tvNoPembayaran);
            tvTanggal = itemView.findViewById(R.id.tvTanggal);
            tvNota = itemView.findViewById(R.id.tvNota);
            tvNilaiNota = itemView.findViewById(R.id.tvNilaiNota);
            tvPembayaran = itemView.findViewById(R.id.tvPembayaran);
            tvSisa = itemView.findViewById(R.id.tvSisa);
            tvKeterangan = itemView.findViewById(R.id.tvKeterangan);
            btnHapus = itemView.findViewById(R.id.btnHapus);
            btnPrint = itemView.findViewById(R.id.btnPrint);

        }
    }

    private String formatTanggal(String tanggal) {
        if (tanggal == null || tanggal.trim().isEmpty()) {
            return "-";
        }

        try {
            SimpleDateFormat formatDatabase =
                    new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

            SimpleDateFormat formatTampilan =
                    new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

            Date date = formatDatabase.parse(tanggal);

            return date != null ? formatTampilan.format(date) : "-";

        } catch (ParseException e) {
            return tanggal;
        }
    }
}