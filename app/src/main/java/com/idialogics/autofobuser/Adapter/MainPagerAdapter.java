package com.idialogics.autofobuser.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.idialogics.autofobuser.Fragments.CartFragment;
import com.idialogics.autofobuser.Fragments.HomeFragment;
import com.idialogics.autofobuser.Fragments.NotificationsFragment;
import com.idialogics.autofobuser.Fragments.ProfileFragment;

@SuppressWarnings("deprecation")
public class MainPagerAdapter extends FragmentStatePagerAdapter {

    public MainPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);

    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {

            return new HomeFragment();

        } else if (position == 1) {

            return new CartFragment();

        } else if (position == 2) {

            return new NotificationsFragment();

        } else if (position == 3) {

            return new ProfileFragment();

        }
        return null;
    }

    @Override
    public int getCount() {
        return 4;
    }


}
