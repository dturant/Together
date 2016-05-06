package com.example.dagna.together.helpers;

import android.support.v4.app.Fragment;
        import android.support.v4.app.FragmentManager;
        import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.dagna.together.InformationTabFragment;
import com.example.dagna.together.ParticipantsTabFragment;


public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if(position==0) {
            return new InformationTabFragment();
        }
        else{
            return new ParticipantsTabFragment();// Which Fragment should be dislpayed by the viewpager for the given position
        }
        // In my case we are showing up only one fragment in all the three tabs so we are
        // not worrying about the position and just returning the TabFragment
    }

    @Override
    public int getCount() {
        return 2;           // As there are only 3 Tabs
    }

}
