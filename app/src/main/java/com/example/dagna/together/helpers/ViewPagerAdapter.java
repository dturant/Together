package com.example.dagna.together.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
        import android.support.v4.app.FragmentManager;
        import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.dagna.together.InformationTabFragment;
import com.example.dagna.together.ParticipantsTabFragment;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.games.multiplayer.Participant;


public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    public ViewPagerAdapter(FragmentManager fm, String event_id, Context context, String json_string, ConnectivityManager connMan) {
        super(fm);
        InformationTabFragment.event_id = event_id;
        InformationTabFragment.context = context;
        InformationTabFragment.json_string = json_string;
        InformationTabFragment.connMan = connMan;

        ParticipantsTabFragment.event_id = event_id;
        ParticipantsTabFragment.context = context;
        ParticipantsTabFragment.json_string = json_string;
        ParticipantsTabFragment.connMan = connMan;
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
