package com.insoft.laris.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.insoft.laris.Interface.masterBarangInterface;
import com.insoft.laris.R;
import com.insoft.laris.model.Produk;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ItemMasterBarang extends RecyclerView.Adapter<ItemMasterBarang.ViewHolder> {

    private Context context;
    private List<Produk> produks;
    private masterBarangInterface masterbaranginterface;

    public ItemMasterBarang(Context context, List<Produk> produks, masterBarangInterface masterbaranginterface) {
        this.context = context;
        this.produks = produks;
        this.masterbaranginterface = masterbaranginterface;


    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.itembarangmaster, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);

        holder.namabarang.setText(produks.get(position).getNm_barang());
        holder.satuan.setText(produks.get(position).getSatuan());
        holder.konversi.setText("Konversi ( "+String.valueOf(produks.get(position).getKonversi())+" )");
        holder.stok.setText("Stok ( "+String.valueOf(produks.get(position).getStok())+" )");
        holder.harga_jual.setText(formatRupiah.format(produks.get(position).getHarga_jual()));
        holder.harga_reseller.setText(formatRupiah.format(produks.get(position).getHarga_reseller()));

        holder.rootlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                masterbaranginterface.pilihProduk(position);
            }
        });

        holder.rootlayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                masterbaranginterface.hapusProduk(position);
                return false;
            }
        });


    }

    @Override
    public int getItemCount() {
        return produks.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView namabarang, satuan, konversi, stok, harga_jual, harga_reseller;
        private LinearLayout rootlayout;

        public ViewHolder(View itemView) {
            super(itemView);
            rootlayout = itemView.findViewById(R.id.rootlayout);
            namabarang = itemView.findViewById(R.id.namabarang);
            satuan = itemView.findViewById(R.id.satuan);
            konversi = itemView.findViewById(R.id.konversi);
            stok = itemView.findViewById(R.id.stok);
            harga_jual = itemView.findViewById(R.id.harga);
            harga_reseller = itemView.findViewById(R.id.harga_reseller);
        }
    }
}
