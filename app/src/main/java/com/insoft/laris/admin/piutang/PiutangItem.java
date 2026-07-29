package com.insoft.laris.admin.piutang;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.insoft.laris.Interface.pelangganInterface;
import com.insoft.laris.R;
import com.insoft.laris.model.Pelanggan;
import com.insoft.laris.utils.MyDatabaseHelper;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PiutangItem extends RecyclerView.Adapter<PiutangItem.ViewHolder> {

    private Context context;
    private List<Piutang> piutang;



    public PiutangItem(Context context, List<Piutang> piutang) {
        this.context = context;
        this.piutang = piutang;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_piutang, parent, false);
        return new ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(PiutangItem.ViewHolder holder,  final int position) {

        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        holder.tvNamaPelanggan.setText(piutang.get(position).getNm_pelanggan());
        holder.tvInvoice.setText(piutang.get(position).getNota());
        int sisaPiutang = piutang.get(position).getSisa();
        if(sisaPiutang > 0) {
            holder.tvStatus.setText("OUTSTANDING");
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_outstanding);
        } else {
            holder.tvStatus.setText("LUNAS");
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_lunas);
        }

        holder.tvTotalBelanja.setText(formatRupiah.format(piutang.get(position).getBelanja()));
        holder.tvSudahDibayar.setText(formatRupiah.format(piutang.get(position).getBayar()));
        holder.tvSisaPiutang.setText(formatRupiah.format(piutang.get(position).getSisa()));
        holder.tvJatuhTempo.setText("tanggal jatuh tempo : "+formatTanggal(String.valueOf(piutang.get(position).getJatuh_tempo())));
        holder.tvTanggal.setText(formatTanggal(String.valueOf(piutang.get(position).getTanggal())));

        holder.rootlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });




    }

    @Override
    public int getItemCount() {
        return piutang.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvNamaPelanggan,tvInvoice, tvStatus, tvTotalBelanja, tvSudahDibayar, tvSisaPiutang, tvJatuhTempo, tvTanggal;
        private MaterialCardView rootlayout;

        public ViewHolder(View itemView) {
            super(itemView);
            rootlayout = itemView.findViewById(R.id.rootLayout);

            tvNamaPelanggan = itemView.findViewById(R.id.tvNamaPelanggan);
            tvInvoice = itemView.findViewById(R.id.tvInvoice);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvTotalBelanja = itemView.findViewById(R.id.tvTotalBelanja);
            tvSudahDibayar = itemView.findViewById(R.id.tvSudahDibayar);
            tvSisaPiutang = itemView.findViewById(R.id.tvSisaPiutang);
            tvJatuhTempo = itemView.findViewById(R.id.tvJatuhTempo);
            tvTanggal = itemView.findViewById(R.id.tvTanggal);
        }
    }

    private String formatTanggal(String tanggal) {
        if (tanggal == null || tanggal.trim().isEmpty()) {
            return "-";
        }

        try {
            SimpleDateFormat formatDatabase =
                    new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

            SimpleDateFormat formatTampilan =
                    new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

            Date date = formatDatabase.parse(tanggal);

            return date != null ? formatTampilan.format(date) : "-";

        } catch (ParseException e) {
            return tanggal;
        }
    }
}