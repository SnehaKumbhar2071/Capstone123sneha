package com.example.home;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class viewpagermessengerAdapter extends FragmentPagerAdapter {

    public viewpagermessengerAdapter(FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new information(); // Your first fragment
            case 1:
                return new image();
            case 2:
                return new prescript();
            case 3:
                return new payment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return "Info";
        } else if (position == 1) {
            return "Images";
        } else if (position == 2) {
            return "Prescript";
        } else {
            return "Pay";
        }
    }
}
