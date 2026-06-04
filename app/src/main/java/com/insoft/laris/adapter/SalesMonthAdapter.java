package com.insoft.laris.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.insoft.laris.R;
import com.insoft.laris.model.SalesMonth;
import com.insoft.laris.report.PerInvoiceActivity;
import com.insoft.laris.utils.SessionManager;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class SalesMonthAdapter extends RecyclerView.Adapter<SalesMonthAdapter.ViewHolder>  {

    private Context context;
    private List<SalesMonth> laporan;
    private SessionManager sessionManager;

    public SalesMonthAdapter(Context context, List<SalesMonth> laporan) {
        this.context = context;
        this.laporan = laporan;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_sales_month, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        sessionManager = new SessionManager(context);
        HashMap<String, String> user = sessionManager.getSessionData();
        String iduser = user.get(sessionManager.ID);

        holder.tanggalpenjualan.setText(laporan.get(position).getTanggal_penjualan());
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        holder.nilaipenjualan.setText(formatRupiah.format(laporan.get(position).getBelanja()));
        holder.rootlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PerInvoiceActivity.class);
                intent.putExtra("sekarang", laporan.get(position).getTanggal_penjualan());
                intent.putExtra("userid", iduser);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return laporan.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tanggalpenjualan, nilaipenjualan;
        private LinearLayout rootlayout;
        public ViewHolder(View itemView) {
        super(itemView);
            tanggalpenjualan = itemView.findViewById(R.id.tanggalpenjualan);
            nilaipenjualan = itemView.findViewById(R.id.nilaipenjualan);
            rootlayout = itemView.findViewById(R.id.rootlayout);

        }
    }

}
