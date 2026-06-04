package com.insoft.laris.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.insoft.laris.fragments.SalesTanggalFragment;
import com.insoft.laris.fragments.SalesTanggalItemFragment;


public class FragmentTanggalAdapter extends FragmentStateAdapter {
    public FragmentTanggalAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position)
        {
            case 1 :
                return new SalesTanggalItemFragment();

        }

        return new SalesTanggalFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
