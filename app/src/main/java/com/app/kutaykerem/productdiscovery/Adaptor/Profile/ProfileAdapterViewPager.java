package com.app.kutaykerem.productdiscovery.Adaptor.Profile;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.app.kutaykerem.productdiscovery.Pages.ProfileAnlikDusuncelerFragment;
import com.app.kutaykerem.productdiscovery.Pages.ProfileGonderilerFragment;

import java.util.ArrayList;
import java.util.List;


public class ProfileAdapterViewPager extends FragmentStateAdapter {

    private List<Fragment> fragList;

    public ProfileAdapterViewPager(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        fragList = new ArrayList<>();
        fragList.add(new ProfileGonderilerFragment());
        fragList.add(new ProfileAnlikDusuncelerFragment());
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
