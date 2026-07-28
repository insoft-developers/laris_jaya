package com.insoft.laris.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.insoft.laris.R;
import com.insoft.laris.model.Item;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;



public class DetailAdapter extends RecyclerView.Adapter<DetailAdapter.ViewHolder>  {

    private Context context;
    private List<Item> items;


    public DetailAdapter(Context context, List<Item> items) {
        this.context = context;
        this.items = items;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.itemdetail2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);

        holder.namaproduk.setText(items.get(position).getNm_barang());
        String idproduk = items.get(position).getKd_barang();
        holder.hargasatuan.setText(formatRupiah.format(items.get(position).getHarga()));
        holder.totalharga.setText(formatRupiah.format(items.get(position).getTotal()));
        holder.quantity.setText(String.valueOf(items.get(position).getJumlah()));

        holder.subtotal.setText(formatRupiah.format(items.get(position).getSubtotal()));
        holder.discount.setText(formatRupiah.format(items.get(position).getDisk()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView namaproduk, hargasatuan, totalharga, quantity, subtotal, discount;
        private ImageView fotoproduk;
        private LinearLayout rootlayout;
        public ViewHolder(View itemView) {
            super(itemView);

            namaproduk = itemView.findViewById(R.id.namaproduk);
            hargasatuan = itemView.findViewById(R.id.hargasatuan);
            totalharga = itemView.findViewById(R.id.totalharga);
            quantity = itemView.findViewById(R.id.quantity);
            subtotal = itemView.findViewById(R.id.subtotal);
            discount = itemView.findViewById(R.id.discount);

            rootlayout = itemView.findViewById(R.id.rootlayout);

        }
    }

}
