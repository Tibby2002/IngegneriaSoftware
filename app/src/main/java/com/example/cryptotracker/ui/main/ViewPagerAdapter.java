package com.example.cryptotracker.ui.main;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.cryptotracker.CancellaAddress;
import com.example.cryptotracker.ModificaAddress;
import com.google.android.material.tabs.TabLayout;

public class ViewPagerAdapter extends FragmentStateAdapter {
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new ModificaAddress();
            case 1:
                return new CancellaAddress();
            default:
                return new ModificaAddress();

        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
