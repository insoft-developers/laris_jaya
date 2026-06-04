package com.insoft.laris.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.insoft.laris.fragments.SalesTodayFragment;
import com.insoft.laris.fragments.SalesTodayItemFragment;


public class FragmentAdapter extends FragmentStateAdapter {
    public FragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position)
        {
            case 1 :
                return new SalesTodayItemFragment();

        }

        return new SalesTodayFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
