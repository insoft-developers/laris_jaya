package com.insoft.laris.adapter;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.insoft.laris.R;
import com.insoft.laris.model.SalesToday;
import com.insoft.laris.report.DetailItemActivity;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class SalesTodayAdapter extends RecyclerView.Adapter<SalesTodayAdapter.ViewHolder>  {

    private Context context;
    private List<SalesToday> laporan;

    public SalesTodayAdapter(Context context, List<SalesToday> laporan) {
        this.context = context;
        this.laporan = laporan;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_sales_today, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.invoice.setText(laporan.get(position).getNota());
        holder.namacustomer.setText(laporan.get(position).getNm_pelanggan());
        holder.tanggal.setText(laporan.get(position).getTanggal());
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        holder.totalpenjualan.setText(formatRupiah.format(laporan.get(position).getBelanja()));
        holder.subtotal.setText(formatRupiah.format(laporan.get(position).getSubtotal()));
        holder.discount.setText(formatRupiah.format(laporan.get(position).getTotal_discount()));
        holder.pembayaran.setText(formatRupiah.format(laporan.get(position).getBayar()));
        int xpembayaran = laporan.get(position).getBayar();
        int xbelanja = laporan.get(position).getBelanja();

        if(xpembayaran >= xbelanja) {
            holder.lblkembalian.setText("Kembalian");
            holder.lunas.setVisibility(VISIBLE);
            holder.kembalian.setTextColor(
                    Color.parseColor("#059669")
            );
        } else {
            holder.lblkembalian.setText("BLM DIBAYAR");

            holder.lunas.setVisibility(GONE);
            holder.kembalian.setTextColor(
                    Color.parseColor("#DC2626")
            );
        }

        holder.kembalian.setText(formatRupiah.format(laporan.get(position).getKembali()));

        holder.rootlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailItemActivity.class);
                intent.putExtra("invoice", laporan.get(position).getNota());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return laporan.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView invoice, namacustomer, tanggal, totalpenjualan, subtotal, discount, pembayaran, kembalian, lblkembalian, lunas;
        private ImageView fotocustomer;
        private LinearLayout rootlayout;
        public ViewHolder(View itemView) {
        super(itemView);
            invoice = itemView.findViewById(R.id.invoice);
            namacustomer = itemView.findViewById(R.id.namacustomer);
            tanggal = itemView.findViewById(R.id.tanggal);
            totalpenjualan = itemView.findViewById(R.id.totalpenjualan);
            subtotal = itemView.findViewById(R.id.subtotalpenjualan);
            discount = itemView.findViewById(R.id.discountpenjualan);
            pembayaran = itemView.findViewById(R.id.pembayaran);
            kembalian =itemView.findViewById(R.id.kembalian);
            lblkembalian = itemView.findViewById(R.id.lblkembalian);
            lunas = itemView.findViewById(R.id.lunas);



            rootlayout = itemView.findViewById(R.id.rootlayout);

        }
    }

}
