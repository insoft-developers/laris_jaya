package com.insoft.laris.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.insoft.laris.Interface.penjualanInterface;
import com.insoft.laris.R;
import com.insoft.laris.model.Penjualan;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ItemPenjualan extends RecyclerView.Adapter<ItemPenjualan.ViewHolder> {

    private Context context;
    private List<Penjualan> penjualan;
    private penjualanInterface penjualaninterface;

    public ItemPenjualan(Context context, List<Penjualan> penjualan, penjualanInterface penjualaninterface) {
        this.context = context;
        this.penjualan = penjualan;
        this.penjualaninterface = penjualaninterface;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.itemdetailjual, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        holder.namaProduk.setText(penjualan.get(position).getNm_barang());
        holder.hargaSatuan.setText(formatRupiah.format(penjualan.get(position).getHarga()));
        holder.jumlah.setText(String.valueOf(penjualan.get(position).getJumlah()));
        holder.totalHarga.setText(formatRupiah.format(penjualan.get(position).getTotal()));
        holder.rootlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                penjualaninterface.pilihItem(position);
            }
        });

        holder.rootlayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                penjualaninterface.hapusItem(position);
                return false;
            }
        });


    }

    @Override
    public int getItemCount() {
        return penjualan.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView namaProduk, hargaSatuan, jumlah, totalHarga ;
        private LinearLayout rootlayout;

        public ViewHolder(View itemView) {
            super(itemView);
            rootlayout = itemView.findViewById(R.id.list_item);
            namaProduk = itemView.findViewById(R.id.namaproduk);
            hargaSatuan = itemView.findViewById(R.id.hargasatuan);
            jumlah = itemView.findViewById(R.id.jumlah);
            totalHarga = itemView.findViewById(R.id.totalharga);
        }
    }
}
