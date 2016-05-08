package com.example.dagna.together;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapActivity extends Activity {
    static final LatLng HAMBURG = new LatLng(53.558, 9.927);
    static final LatLng KIEL = new LatLng(53.551, 9.993);
    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        setContentView(R.layout.activity_map);
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                .getMap();

        String address = getIntent().getExtras().getString("address");
        final String name = getIntent().getExtras().getString("name");

        Log.d("address", address);


        getCoordinates(address, new OnGeocoderFinishedListener() {
            @Override
            public void onFinished(LatLng result) {
                if (result != null) {
                    Marker city = map.addMarker(new MarkerOptions().position(result)
                            .title(name));
                    Log.e("dsadsadsa", result.toString());
                    // Move the camera instantly to hamburg with a zoom of 15.
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(result, 2));

                    // Zoom in, animating the camera.
                    map.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
                } else {
                    //maybe do sth?
                    Log.e("NIE MA CHUJA", "DSADSA");
                }
            }
        });
    }

    public abstract class OnGeocoderFinishedListener {
        public abstract void onFinished(LatLng result);
    }

    public void getCoordinates(final String address, final OnGeocoderFinishedListener listener) {
        final Context con = getApplicationContext();
        Log.e("PIES TO JEBAL", "LALA");
        new AsyncTask<Void, Integer, LatLng>() {
            @Override
            protected LatLng doInBackground(Void... arg0) {
                Geocoder coder = new Geocoder(con);
                LatLng result;
                List<Address> addresses = null;
                try {
                    addresses = coder.getFromLocationName(address, 1);
                } catch (IOException e) {
                    // nothing
                    Log.e("PRZEJEBANE",e.toString());
                }
                Log.d("addresses", Integer.toString(addresses.size()));
                if(addresses.size() > 0) {
                    result = new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
                    return result;
                }
                else
                    return  null;
            }

            @Override
            protected void onPostExecute(LatLng result) {
                if (result != null && listener != null) {
                    listener.onFinished(result);
                }
            }
        }.execute();

    }


}
