package com.insoft.laris.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.insoft.laris.Interface.barangInterface;
import com.insoft.laris.R;
import com.insoft.laris.model.Produk;
import com.insoft.laris.utils.MyDatabaseHelper;

import java.util.List;

public class ItemBarang extends RecyclerView.Adapter<ItemBarang.ViewHolder> {

    private Context context;
    private MyDatabaseHelper db;
    private List<Produk> produks;
    private barangInterface baranginterface;

    public ItemBarang(Context context, List<Produk> produks, barangInterface baranginterface) {
        this.context = context;
        this.produks = produks;
        this.baranginterface = baranginterface;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.itembarang, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.namabarang.setText(produks.get(position).getNm_barang());
        holder.satuan.setText(produks.get(position).getSatuan());
        holder.konversi.setText(String.valueOf(produks.get(position).getKonversi()));
        holder.rootlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                baranginterface.pilih_barang(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return produks.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView namabarang, satuan, konversi;
        private LinearLayout rootlayout;

        public ViewHolder(View itemView) {
            super(itemView);
            rootlayout = itemView.findViewById(R.id.rootlayout);
            namabarang = itemView.findViewById(R.id.namabarang);
            satuan = itemView.findViewById(R.id.satuan);
            konversi = itemView.findViewById(R.id.konversi);
        }
    }
}
