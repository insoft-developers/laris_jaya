
package com.insoft.laris.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.insoft.laris.R;
import com.insoft.laris.model.DetailStrukItem;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class DetailStrukAdapter extends RecyclerView.Adapter<DetailStrukAdapter.ViewHolder> {

    private final List<DetailStrukItem> items;

    private final NumberFormat formatRupiah =
            NumberFormat.getInstance(new Locale("id", "ID"));

    public DetailStrukAdapter(List<DetailStrukItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_detail_struk, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position
    ) {
        DetailStrukItem item = items.get(position);

        holder.txtNama.setText(item.getNm_barang());

        holder.txtJumlah.setText(
                item.getJumlah() + " " + item.getSatuan()
        );

        holder.txtHarga.setText(
                formatRupiah.format(item.getHarga())
        );

        holder.txtSubtotal.setText(
                formatRupiah.format(item.getSubtotal())
        );

        holder.txtDiskon.setText(
                "-" + formatRupiah.format(item.getDisk())
        );

        holder.txtTotal.setText(
                formatRupiah.format(item.getTotal())
        );
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtNama;
        TextView txtJumlah;
        TextView txtHarga;
        TextView txtSubtotal;
        TextView txtDiskon;
        TextView txtTotal;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtNama = itemView.findViewById(R.id.txtNama);
            txtJumlah = itemView.findViewById(R.id.txtJumlah);
            txtHarga = itemView.findViewById(R.id.txtHarga);
            txtSubtotal = itemView.findViewById(R.id.txtSubtotal);
            txtDiskon = itemView.findViewById(R.id.txtDiskon);
            txtTotal = itemView.findViewById(R.id.txtTotal);
        }
    }
}
