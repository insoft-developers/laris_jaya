package com.insoft.laris.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.insoft.laris.DetailActivity;
import com.insoft.laris.R;
import com.insoft.laris.utils.MyDatabaseHelper;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class ItemTransaksi extends RecyclerView.Adapter<ItemTransaksi.ViewHolder> {

    private Context context;
    private MyDatabaseHelper db;
    ArrayList<HashMap<String, String>> transaksi;


    public ItemTransaksi(Context context, ArrayList<HashMap<String, String>> transaksi) {
        this.context = context;
        this.transaksi = transaksi;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_transaksi, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        db = new MyDatabaseHelper(context);
        holder.tanggal.setText(transaksi.get(position).get("tanggal"));
        holder.idhold.setText(transaksi.get(position).get("nota"));
        holder.namapelanggan.setText(transaksi.get(position).get("customer_name"));
        holder.alamat.setText(transaksi.get(position).get("customer_address"));
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        holder.totalharga.setText(formatRupiah.format(Integer.parseInt(transaksi.get(position).get("total"))));

        holder.listitem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("id_hold_intent", transaksi.get(position).get("nota"));
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return transaksi.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView namapelanggan, totalharga, alamat, idhold, tanggal;
        private LinearLayout listitem;

        public ViewHolder(View itemView) {
            super(itemView);
            namapelanggan = itemView.findViewById(R.id.namapelanggan);
            totalharga = itemView.findViewById(R.id.total);
            alamat = itemView.findViewById(R.id.alamat);
            idhold = itemView.findViewById(R.id.idhold);
            listitem = itemView.findViewById(R.id.rootlayout);
            tanggal = itemView.findViewById(R.id.tanggal);

        }
    }
}
