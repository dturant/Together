package com.example.dagna.together;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Dagna on 05/05/2016.
 */
public class InformationTabFragment extends Fragment {
    TextView Name, Category, Description, Country, City, Street,User ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.information, container, false);
        return view;
    }
}
