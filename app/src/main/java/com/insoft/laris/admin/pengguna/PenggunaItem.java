package com.insoft.laris.admin.pengguna;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.insoft.laris.Interface.pelangganInterface;
import com.insoft.laris.R;
import com.insoft.laris.model.Pelanggan;
import com.insoft.laris.utils.MyDatabaseHelper;

import java.util.List;

public class PenggunaItem extends RecyclerView.Adapter<PenggunaItem.ViewHolder> {

    private Context context;
    private MyDatabaseHelper db;
    private List<PenggunaModel> pengguna;


    public PenggunaItem(Context context, List<PenggunaModel> pengguna) {
        this.context = context;
        this.pengguna = pengguna;


    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_pengguna, parent, false);
        return new ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(PenggunaItem.ViewHolder holder, final int position) {
        holder.kdPengguna.setText(pengguna.get(position).getKd_pengguna());
        holder.nmPengguna.setText(pengguna.get(position).getNm_pengguna());
        holder.name.setText(pengguna.get(position).getNama());
        holder.level.setText(pengguna.get(position).getLevel());


    }

    @Override
    public int getItemCount() {
        return pengguna.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView kdPengguna, nmPengguna, name, level;
        private LinearLayout rootlayout;

        public ViewHolder(View itemView) {
            super(itemView);
            rootlayout = itemView.findViewById(R.id.rootlayout);
            kdPengguna = itemView.findViewById(R.id.kdPengguna);
            nmPengguna = itemView.findViewById(R.id.nmPengguna);
            name = itemView.findViewById(R.id.name);
            level = itemView.findViewById(R.id.level);
        }
    }
}