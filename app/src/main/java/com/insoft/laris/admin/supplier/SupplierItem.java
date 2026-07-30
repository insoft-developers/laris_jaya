package com.insoft.laris.admin.supplier;


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
import com.insoft.laris.admin.piutang.Piutang;
import com.insoft.laris.admin.piutang.PiutangInterface;
import com.insoft.laris.model.Pelanggan;
import com.insoft.laris.utils.MyDatabaseHelper;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SupplierItem extends RecyclerView.Adapter<SupplierItem.ViewHolder> {

    private Context context;
    private List<Supplier> supplier;
    private SupplierInterface supplierInterface;




    public SupplierItem(Context context, List<Supplier> supplier, SupplierInterface supplierInterface) {
        this.context = context;
        this.supplier = supplier;
        this.supplierInterface = supplierInterface;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_supplier, parent, false);
        return new ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        holder.tvNamaSupplier.setText(supplier.get(position).getNm_supplier());
        holder.tvKodeSupplier.setText(supplier.get(position).getKd_supplier());
        holder.tvKontakSupplier.setText(supplier.get(position).getKontak());
        holder.tvTeleponSupplier.setText(supplier.get(position).getTelepon());
        holder.tvAlamatSupplier.setText(supplier.get(position).getAlamat());
        holder.tvInisialSupplier.setText(buatInisial(supplier.get(position).getNm_supplier()));

        holder.btnHapusSupplier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                supplierInterface.hapusItem(position);
            }
        });


        holder.btnEditSupplier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                supplierInterface.editItem(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return supplier.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNamaSupplier;
        TextView tvKodeSupplier;
        TextView tvKontakSupplier;
        TextView tvTeleponSupplier;
        TextView tvAlamatSupplier;
        TextView tvInisialSupplier;

        MaterialButton btnEditSupplier;
        MaterialButton btnHapusSupplier;
        MaterialCardView rootLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            tvNamaSupplier =
                    itemView.findViewById(
                            R.id.tvNamaSupplier
                    );

            tvKodeSupplier =
                    itemView.findViewById(
                            R.id.tvKodeSupplier
                    );

            tvKontakSupplier =
                    itemView.findViewById(
                            R.id.tvKontakSupplier
                    );

            tvTeleponSupplier =
                    itemView.findViewById(
                            R.id.tvTeleponSupplier
                    );

            tvAlamatSupplier =
                    itemView.findViewById(
                            R.id.tvAlamatSupplier
                    );

            tvInisialSupplier =
                    itemView.findViewById(
                            R.id.tvInisialSupplier
                    );

            btnEditSupplier =
                    itemView.findViewById(
                            R.id.btnEditSupplier
                    );

            btnHapusSupplier =
                    itemView.findViewById(
                            R.id.btnHapusSupplier
                    );


            rootLayout = itemView.findViewById(R.id.rootLayout);


        }
    }


    private String buatInisial(String nama) {
        if (nama == null || nama.trim().isEmpty()) {
            return "S";
        }

        String[] kata = nama.trim().split("\\s+");

        if (kata.length == 1) {
            return kata[0]
                    .substring(0, 1)
                    .toUpperCase(Locale.ROOT);
        }

        return (
                kata[0].substring(0, 1)
                        + kata[1].substring(0, 1)
        ).toUpperCase(Locale.ROOT);
    }
}