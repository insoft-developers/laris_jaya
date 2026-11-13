package com.insoft.laris.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.insoft.laris.R;
import com.insoft.laris.model.RiwayatStruk;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CetakUlangStrukAdapter
        extends RecyclerView.Adapter<CetakUlangStrukAdapter.ViewHolder> {

    private final List<RiwayatStruk> data;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onClick(RiwayatStruk item);
    }

    public CetakUlangStrukAdapter(
            List<RiwayatStruk> data,
            OnItemClickListener listener) {
        this.data = data;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView nota, pelanggan, tanggal, total;

        public ViewHolder(@NonNull View view) {
            super(view);

            nota = view.findViewById(R.id.tvNota);
            pelanggan = view.findViewById(R.id.tvPelanggan);
            tanggal = view.findViewById(R.id.tvTanggal);
            total = view.findViewById(R.id.tvTotal);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(
                        R.layout.item_cetak_ulang_struk,
                        parent,
                        false
                );

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder, int position) {

        RiwayatStruk item = data.get(position);

        holder.nota.setText(item.getNota());

        String nama = item.getNm_pelanggan();

        holder.pelanggan.setText(
                nama == null || nama.trim().isEmpty()
                        ? "Pelanggan Umum"
                        : nama
        );

        holder.tanggal.setText(item.getTanggal());

        NumberFormat rupiah = NumberFormat.getCurrencyInstance(
                new Locale("id", "ID")
        );

        holder.total.setText(rupiah.format(item.getBelanja()));

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
