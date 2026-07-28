package com.insoft.laris.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.google.android.material.card.MaterialCardView;
import com.insoft.laris.R;
import com.insoft.laris.model.SalesTodayItem;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;



public class SalesTodayItemAdapter extends RecyclerView.Adapter<SalesTodayItemAdapter.ViewHolder>  {

    private Context context;
    private List<SalesTodayItem> laporan;

    public SalesTodayItemAdapter(Context context, List<SalesTodayItem> laporan) {
        this.context = context;
        this.laporan = laporan;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_sales_today_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {


        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);

        holder.namaproduk.setText(laporan.get(position).getNm_barang());
        holder.satuanproduk.setText(String.valueOf(laporan.get(position).getJumlahpenjualan())+" "+laporan.get(position).getSatuan());
        holder.nilaipenjualan.setText(formatRupiah.format(laporan.get(position).getNilaipenjualan()));



    }

    @Override
    public int getItemCount() {
        return laporan.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView namaproduk, nilaipenjualan, satuanproduk;
        private ImageView fotoproduk;
        private MaterialCardView rootlayout;
        public ViewHolder(View itemView) {
        super(itemView);
            namaproduk = itemView.findViewById(R.id.namaproduk);
            nilaipenjualan = itemView.findViewById(R.id.nilaipenjualan);
            satuanproduk = itemView.findViewById(R.id.satuanproduk);

            rootlayout = itemView.findViewById(R.id.rootlayout);

        }
    }

}
