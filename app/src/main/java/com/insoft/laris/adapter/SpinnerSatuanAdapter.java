package com.insoft.laris.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.insoft.laris.R;
import com.insoft.laris.model.Satuan;

import java.util.ArrayList;

public class SpinnerSatuanAdapter extends ArrayAdapter<Satuan> {

    public SpinnerSatuanAdapter(Context context,
                                ArrayList<Satuan> satuanList)
    {
        super(context, 0, satuanList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable
            View convertView, @NonNull ViewGroup parent)
    {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable
            View convertView, @NonNull ViewGroup parent)
    {
        return initView(position, convertView, parent);
    }

    private View initView(int position, View convertView,
                          ViewGroup parent)
    {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_spinner, parent, false);
        }

        TextView textViewName = convertView.findViewById(R.id.txt_spinner);
        Satuan currentItem = getItem(position);

        if (currentItem != null) {
            textViewName.setText(currentItem.getNm_satuan());
        }
        return convertView;
    }
}
