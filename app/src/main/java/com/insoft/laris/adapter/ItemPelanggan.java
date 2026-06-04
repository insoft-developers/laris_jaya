package com.insoft.laris.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.insoft.laris.Interface.pelangganInterface;
import com.insoft.laris.R;
import com.insoft.laris.model.Pelanggan;
import com.insoft.laris.utils.MyDatabaseHelper;

import java.util.List;

public class ItemPelanggan extends RecyclerView.Adapter<ItemPelanggan.ViewHolder> {

    private Context context;
    private MyDatabaseHelper db;
    private List<Pelanggan> pelanggans;
    private pelangganInterface pelangganinterface;

    public ItemPelanggan(Context context, List<Pelanggan> pelanggans, pelangganInterface pelangganinterface) {
        this.context = context;
        this.pelanggans = pelanggans;
        this.pelangganinterface = pelangganinterface;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.itempelanggan, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.namapelanggan.setText(pelanggans.get(position).getNm_pelanggan());
        holder.alamat.setText(pelanggans.get(position).getAlamat());
        holder.grup.setText(pelanggans.get(position).getGrup());
        holder.phne.setText(pelanggans.get(position).getTelepon());
        holder.rootlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pelangganinterface.pilih_pelanggan(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return pelanggans.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView namapelanggan, alamat, grup, phne;
        private LinearLayout rootlayout;

        public ViewHolder(View itemView) {
            super(itemView);
            rootlayout = itemView.findViewById(R.id.rootlayout);
            namapelanggan = itemView.findViewById(R.id.namapelanggan);
            alamat = itemView.findViewById(R.id.alamat);
            grup = itemView.findViewById(R.id.grup);
            phne = itemView.findViewById(R.id.phone);
        }
    }
}
