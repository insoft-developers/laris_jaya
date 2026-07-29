package com.insoft.laris.admin.piutang;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
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
    private PiutangInterface piutangInterface;



    public PiutangItem(Context context, List<Piutang> piutang, PiutangInterface piutangInterface) {
        this.context = context;
        this.piutang = piutang;
        this.piutangInterface = piutangInterface;
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
            holder.btnPembayaran.setVisibility(VISIBLE);
        } else {
            holder.tvStatus.setText("LUNAS");
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_lunas);
            holder.btnPembayaran.setVisibility(GONE);
        }

        holder.tvTotalBelanja.setText(formatRupiah.format(piutang.get(position).getBelanja()));
        holder.tvSudahDibayar.setText(formatRupiah.format(piutang.get(position).getBayar()));
        holder.tvSisaPiutang.setText(formatRupiah.format(piutang.get(position).getSisa()));
        holder.tvJatuhTempo.setText("tanggal jatuh tempo : "+formatTanggal(String.valueOf(piutang.get(position).getJatuh_tempo())));
        holder.tvTanggal.setText(formatTanggal(String.valueOf(piutang.get(position).getTanggal())));

        holder.btnPembayaran.setOnClickListener(view -> {
            int currentPosition = holder.getAdapterPosition();

            if (currentPosition == RecyclerView.NO_POSITION) {
                return;
            }

            Piutang dataDipilih = piutang.get(currentPosition);

            if (piutangInterface != null) {
                piutangInterface.onPembayaranClick(dataDipilih);
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
        private MaterialButton btnPembayaran;

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
            btnPembayaran  = itemView.findViewById(R.id.btnPembayaran);
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