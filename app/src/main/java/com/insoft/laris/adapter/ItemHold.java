package com.insoft.laris.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.insoft.laris.Interface.holdInterface;
import com.insoft.laris.R;
import com.insoft.laris.model.Hold;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ItemHold extends RecyclerView.Adapter<ItemHold.ViewHolder> {

    private Context context;
    private holdInterface holdinterface;
    private List<Hold> penjualan;

    public ItemHold(Context context, List<Hold> penjualan, holdInterface holdinterface) {
        this.context = context;
        this.penjualan = penjualan;
        this.holdinterface = holdinterface;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_transaksi, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        holder.idhold.setText(String.valueOf(penjualan.get(position).getId_hold()));
        holder.tanggal.setText(penjualan.get(position).getTanggal_transaksi());
        holder.namapelanggan.setText(penjualan.get(position).getCustomer_name());
        holder.alamat.setText(penjualan.get(position).getCustomer_address());
        holder.total.setText(formatRupiah.format(penjualan.get(position).getTotal_penjualan()));
        holder.rootlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holdinterface.pilihItem(position);
            }
        });

        holder.rootlayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                holdinterface.hapusItem(position);
                return false;
            }
        });

    }

    @Override
    public int getItemCount() {
        return penjualan.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView idhold, tanggal, namapelanggan, alamat, total;
        private LinearLayout rootlayout;

        public ViewHolder(View itemView) {
            super(itemView);
            rootlayout = itemView.findViewById(R.id.rootlayout);
            idhold = itemView.findViewById(R.id.idhold);
            tanggal = itemView.findViewById(R.id.tanggal);
            namapelanggan = itemView.findViewById(R.id.namapelanggan);
            alamat = itemView.findViewById(R.id.alamat);
            total = itemView.findViewById(R.id.total);
        }
    }
}
