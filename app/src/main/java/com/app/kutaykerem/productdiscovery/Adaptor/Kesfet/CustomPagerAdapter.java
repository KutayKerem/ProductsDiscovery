package com.app.kutaykerem.productdiscovery.Adaptor.Kesfet;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.app.kutaykerem.productdiscovery.Pages.KesfetAnlikDusuncelerFragment;
import com.app.kutaykerem.productdiscovery.Pages.KesfetFragment;

import java.util.ArrayList;
import java.util.List;


public class CustomPagerAdapter extends FragmentStateAdapter {

    private List<Fragment> fragList;

    public CustomPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        fragList = new ArrayList<>();
        fragList.add(new KesfetFragment());
        fragList.add(new KesfetAnlikDusuncelerFragment());
    }



    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragList.get(position);
    }

    @Override
    public int getItemCount() {
        return fragList.size();
    }



}