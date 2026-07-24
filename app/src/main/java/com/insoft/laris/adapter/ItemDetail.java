package com.insoft.laris.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.insoft.laris.Interface.itemInterface;
import com.insoft.laris.R;
import com.insoft.laris.utils.MyDatabaseHelper;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class ItemDetail extends RecyclerView.Adapter<ItemDetail.ViewHolder> {

    private Context context;
    private MyDatabaseHelper db;
    ArrayList<HashMap<String, String>> pesanan;
    private itemInterface iteminterface;

    public ItemDetail(Context context, ArrayList<HashMap<String, String>> pesanan) {
        this.context = context;
        this.pesanan = pesanan;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.itemtransaksi, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        db = new MyDatabaseHelper(context);
        holder.namaproduk.setText(pesanan.get(position).get("nm_barang"));
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        holder.totalharga.setText(formatRupiah.format(Integer.parseInt(pesanan.get(position).get("total"))));
        holder.jumlah.setText(pesanan.get(position).get("jumlah"));
        holder.hargasatuan.setText(formatRupiah.format(Integer.parseInt(pesanan.get(position).get("harga"))));
        holder.subtotal.setText(formatRupiah.format(Integer.parseInt(pesanan.get(position).get("subtotal"))));
        holder.discount.setText(formatRupiah.format(Integer.parseInt(pesanan.get(position).get("disk"))));


    }

    @Override
    public int getItemCount() {
        return pesanan.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView namaproduk, totalharga, jumlah, hargasatuan, subtotal, discount;
        private LinearLayout listitem;

        public ViewHolder(View itemView) {
            super(itemView);
            namaproduk = itemView.findViewById(R.id.namaproduk);
            totalharga = itemView.findViewById(R.id.totalharga);
            hargasatuan = itemView.findViewById(R.id.hargasatuan);
            jumlah = itemView.findViewById(R.id.jumlah);
            subtotal = itemView.findViewById(R.id.subtotal);
            discount = itemView.findViewById(R.id.discount);

            listitem = itemView.findViewById(R.id.list_item);




        }
    }
}
