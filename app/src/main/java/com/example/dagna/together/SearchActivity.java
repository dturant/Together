package com.example.dagna.together;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.dagna.together.helpers.EventAdapter;
import com.example.dagna.together.helpers.Events;
import com.example.dagna.together.onlineDatabase.DisplayEvents;
import com.example.dagna.together.onlineDatabase.GetCategories;
import com.example.dagna.together.onlineDatabase.GetParticularEvents;
import com.example.dagna.together.services.DatabaseIntentService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{
    String json_string;
    JSONObject jsonObject;
    JSONArray jsonArray;
    List<String> resultCategories;

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private boolean isLocationAvailable()
    {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            return true;
        }else{
            return false;
        }
    }

    private void displayToast(int val)
    {
        switch(val)
        {
            case 1:
                Toast.makeText(this, R.string.offline_mode,
                        Toast.LENGTH_LONG).show();
                break;
            case 2:
                Toast.makeText(this, R.string.gps_off,
                        Toast.LENGTH_LONG).show();
                break;
            case 3:
                String text = getResources().getString(R.string.offline_mode) + "/" + getResources().getString(R.string.gps_off);;
                Toast.makeText(this, text,
                        Toast.LENGTH_LONG).show();
                break;
            default: break;

        }
    }

    protected void createNetErrorDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("You need a network connection to perform this action. Please turn on mobile network or Wi-Fi in Settings.")
                .setTitle("Unable to connect")
                .setCancelable(false)
                .setPositiveButton("Settings",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent i = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                                startActivity(i);
                            }
                        }
                )
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                displayToast(1);
                            }
                        }
                );
        AlertDialog alert = builder.create();
        alert.show();
    }


    /*private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {

                String result = bundle.getString(DatabaseIntentService.RESULT);
                if(result == DatabaseIntentService.RESULT_FAIL)
                {
                    //fail
                }
                else {
                    goToResult();
                }
            }
        }
    };
    */

    EditText Name, Country, City;
    private RadioGroup radioCity;
    private RadioButton radioLocationButton, radioTextButton;
    Spinner Category;

    private void fillSpinner(List<String> list){
        Category = (Spinner) findViewById(R.id.search_event_category);

        List<String> categories = list;

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        Category.setAdapter(dataAdapter);
    }

    private void getCategories(){
        final ArrayList<String> categories=new ArrayList<>();
        GetCategories getCategories = (GetCategories) new GetCategories(new GetCategories.AsyncResponse() {

            @Override
            public void processFinish(String output) {
                if(GetCategories.json_string==null){
                    Toast.makeText(getApplicationContext(), "first get json", Toast.LENGTH_LONG).show();
                }
                else{
                    json_string=GetCategories.json_string;

                    try {
                        jsonObject=new JSONObject(json_string);
                        jsonArray=jsonObject.getJSONArray("server_response");

                        int count=0;
                        String name, description;

                        while(count<jsonArray.length()){
                            JSONObject JO = jsonArray.getJSONObject(count);
                            name=JO.getString("name");
                            categories.add(name);

                            count++;

                        }

                        fillSpinner(categories);

                        Log.d("categories innnnn", categories.toString());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).execute();
    }

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        City = (EditText) findViewById(R.id.search_event_city);

        this.getCategories();

        addListenerOnButton();

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    public void addListenerOnButton() {

        radioCity = (RadioGroup) findViewById(R.id.radioCity);
        radioLocationButton = (RadioButton) findViewById(R.id.radioLocation);
        radioTextButton = (RadioButton) findViewById(R.id.radioText);


        radioCity.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup radioCity, int checkedId) {
                Log.e("O KURWAAA", "" + checkedId);
                if(checkedId == R.id.radioLocation)
                {
                    City.setEnabled(false);
                    City.setInputType(InputType.TYPE_NULL);
                }
                else
                {
                    City.setEnabled(true);
                    City.setInputType(InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE);
                }
            }
        });

    }

    @Override
    public void onPause()
    {
        super.onPause();
      //  unregisterReceiver(receiver);
    }

    @Override
    public void onResume()
    {
        super.onResume();
      //  registerReceiver(receiver, new IntentFilter(DatabaseIntentService.NOTIFICATION));
    }

    public void search(View view) {
        //if czy js wlaczony i czy nie jest null
        if(isNetworkAvailable())
        {
            if (isLocationAvailable())
            {
                //get city
                //getEventsFromOnlineDB("Dornbirn");
            }
            else
            {
                displayToast(2);
                //getEventsFromOnlineDB("Dornbirn");
                Log.e(":D:D:D", "DDSDS");
            }
        }
        else
        {
            if (isLocationAvailable())
            {
                //get city
                displayToast(1);
                //getEventsFromLocalDB("Dornbirn");
                Log.e("TUTAJ", "LALALALALA");
            }
            else
            {
                displayToast(3);
                //getEventsFromLocalDB("Dornbirn");
                Log.e("TUTAJSON", "LALALA");
            }
        }
        Location loc = getLastKnownLocation();
        Toast.makeText(this, String.valueOf(loc.getLatitude()) + "/" + String.valueOf(loc.getLongitude()),
                Toast.LENGTH_LONG).show();

        getCityName(loc, new OnGeocoderFinishedListener() {
            @Override
            public void onFinished(List<Address> results) {
                displayCity(results);
            }
        });

//        if(isNetworkAvailable())
//        {
//            String city, category;
//            city = City.getText().toString();
//            category = Category.getSelectedItem().toString();
//
//            GetParticularEvents getParticularEvents = (GetParticularEvents) new GetParticularEvents(new GetParticularEvents.AsyncResponse() {
//
//                @Override
//                public void processFinish(String output) {
//                    if(GetParticularEvents.json_string==null){
//                        Toast.makeText(getApplicationContext(),"first get json", Toast.LENGTH_LONG).show();
//                    }
//                    else{
//                        json_string=GetParticularEvents.json_string;
//                        Intent intent = new Intent(getApplicationContext(), SearchResultsActivity.class);
//                        intent.putExtra("json_data", json_string);
//                        startActivity(intent);
//                    }
//                }
//            }).execute(city,category);
//        }
//        else
//        {
//            createNetErrorDialog();
//        }
    }

    private void displayCity(List<Address> results)
    {
        Toast.makeText(this, results.get(0).getLocality().toString(),
                Toast.LENGTH_LONG).show();
    }


    public abstract class OnGeocoderFinishedListener {
        public abstract void onFinished(List<Address> results);
    }

    public void getCityName(final Location location, final OnGeocoderFinishedListener listener) {
        final Context con = getApplicationContext();
        new AsyncTask<Void, Integer, List<Address>>() {
            @Override
            protected List<Address> doInBackground(Void... arg0) {
                Geocoder coder = new Geocoder(con, Locale.ENGLISH);
                List<Address> results = null;
                try {
                    results = coder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                } catch (IOException e) {
                    // nothing
                }
                return results;
            }

            @Override
            protected void onPostExecute(List<Address> results) {
                if (results != null && listener != null) {
                    listener.onFinished(results);
                }
            }
        }.execute();
    }

    private void goToResult()
    {
        Intent intent = new Intent(this, SearchResultsActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        mGoogleApiClient.connect();
        //Log.e("Connected?", String.valueOf(mGoogleApiClient.isConnected()));
    }

    @Override
    protected  void onStop()
    {
        mGoogleApiClient.disconnect();
        super.onStop();
    }



    @Override
    public void onConnected(Bundle bundle) {
//        Log.e("ConnectedON?", String.valueOf(mGoogleApiClient.isConnected()));
//        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
//                mGoogleApiClient);
//        if (mLastLocation != null) {
//            Log.e("JEEEEEE", "DDDS");
//            Toast.makeText(this, String.valueOf(mLastLocation.getLatitude()) + "/" + String.valueOf(mLastLocation.getLongitude()),
//                    Toast.LENGTH_LONG).show();
//        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private Location getLastKnownLocation() {
        LocationManager mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = mLocationManager.getLastKnownLocation(provider);
            Log.d("last known locati", provider);

            if (l == null) {
                continue;
            }
            if (bestLocation == null) {
                Log.d("known location: %s", l.toString());
                bestLocation = l;
            }
        }
        if (bestLocation == null) {
            return null;
        }
        return bestLocation;
    }
}
