package com.example.dagna.together;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.dagna.together.helpers.EventAdapter;
import com.example.dagna.together.helpers.Events;
import com.example.dagna.together.helpers.GeneralHelpers;
import com.example.dagna.together.onlineDatabase.DisplayEvents;
import com.example.dagna.together.onlineDatabase.GetCategories;
import com.example.dagna.together.onlineDatabase.GetParticularEvents;
import com.example.dagna.together.onlineDatabase.GetUserById;
import com.example.dagna.together.onlineDatabase.GetUserByLogin;
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

    EditText Name, Country, City, Login;
    TextView Error;
    private RadioGroup radioCity;
    private RadioButton radioLocationButton, radioTextButton;
    Spinner Category;

    public static Context context;
    String currentUser;

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = this.getApplicationContext();

        City = (EditText) findViewById(R.id.search_event_city);
        Login=(EditText) findViewById(R.id.search_user_login);
        Error = (TextView) findViewById(R.id.search_user_error);

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

    public void addListenerOnButton() {

        radioCity = (RadioGroup) findViewById(R.id.radioCity);
        radioLocationButton = (RadioButton) findViewById(R.id.radioLocation);
        radioTextButton = (RadioButton) findViewById(R.id.radioText);


        radioCity.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioCity, int checkedId) {
                Log.e("O KURWAAA", "" + checkedId);
                if (checkedId == R.id.radioLocation) {
                    City.setEnabled(false);
                    City.setInputType(InputType.TYPE_NULL);
                } else {
                    City.setEnabled(true);
                    City.setInputType(InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE);
                }
            }
        });

    }

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

    public void search(View view) {
        //if czy js wlaczony i czy nie jest null
        if(GeneralHelpers.isNetworkAvailable((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE)))
        {
            if (radioCity.getCheckedRadioButtonId() == R.id.radioLocation && GeneralHelpers.isLocationAvailable((LocationManager) getSystemService(LOCATION_SERVICE)))
            {
                Location loc = getLastKnownLocation();
                Toast.makeText(this, String.valueOf(loc.getLatitude()) + "/" + String.valueOf(loc.getLongitude()),
                        Toast.LENGTH_LONG).show();

                getCityName(loc, new OnGeocoderFinishedListener() {
                    @Override
                    public void onFinished(List<Address> results) {
                        if (results.get(0) != null) {
                            String city = results.get(0).getLocality().toString();
                            searchEvents(city);
                        } else {
                            //maybe do sth?
                        }

//                        displayCity(results);
                    }
                });
            }
            else if (radioCity.getCheckedRadioButtonId() == R.id.radioLocation)
            {
                GeneralHelpers.createGpsErrorDialog(this);
            }
            else
            {
                String city = City.getText().toString();
                searchEvents(city);
            }
        }
        else
        {
            GeneralHelpers.createNetErrorDialog(this);
        }
    }

    public void searchEvents(String city){
        String category;
        category = Category.getSelectedItem().toString();

        GetParticularEvents getParticularEvents = (GetParticularEvents) new GetParticularEvents(new GetParticularEvents.AsyncResponse() {

            @Override
            public void processFinish(String output) {
                if(GetParticularEvents.json_string==null){
                    Toast.makeText(getApplicationContext(),"first get json", Toast.LENGTH_LONG).show();
                }
                else{
                    json_string=GetParticularEvents.json_string;
                    Intent intent = new Intent(getApplicationContext(), SearchResultsActivity.class);
                    intent.putExtra("json_data", json_string);
                    startActivity(intent);
                }
            }
        }).execute(city,category);

    }

    public void searchUsers(View view){
        final String login = Login.getText().toString();

        GetUserByLogin getUserByLogin = (GetUserByLogin) new GetUserByLogin(new GetUserByLogin.AsyncResponse() {

            @Override
            public void processFinish(String output) {
                Log.d("USERS ID OUTPUT", output);
                if (GetUserByLogin.json_string.length() < 30) {
                    Log.d("fail!!!!", "fail :(");
                    //dodac text view
                    Error.setVisibility(View.VISIBLE);


                } else {
                    //Log.d("data!!!!!!!", GetUserByLogin.json_string);
                    json_string = GetUserByLogin.json_string;

                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                    currentUser = preferences.getString("login", "");

                    if(!currentUser.equals(login)) {

                        Intent intent = new Intent(getApplicationContext(), UsersProfileActivity.class);
                        intent.putExtra("json_data", json_string);
                        //intent.putExtra("user_id", userId);
                        //Log.d("event_id from timeline", userId);
                        startActivity(intent);
                    }
                    else{
                        Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                        intent.putExtra("json_data", json_string);
                        //intent.putExtra("user_id", userId);
                        //Log.d("event_id from timeline", userId);
                        startActivity(intent);
                    }

                }
            }
        }).execute(login);
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

    private void displayCity(List<Address> results)
    {
        Toast.makeText(this, results.get(0).getLocality().toString(),
                Toast.LENGTH_LONG).show();
    }

    private void goToResult()
    {
        Intent intent = new Intent(this, SearchResultsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onConnected(Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    //Helpers
//    public boolean isNetworkAvailable() {
//        ConnectivityManager connectivityManager
//                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
//        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
//    }
//
//    public boolean isLocationAvailable()
//    {
//        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//
//        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
//            return true;
//        }else{
//            return false;
//        }
//    }
//
//    public void displayToast(int val)
//    {
//        switch(val)
//        {
//            case 1:
//                Toast.makeText(this, R.string.offline_mode,
//                        Toast.LENGTH_LONG).show();
//                break;
//            case 2:
//                Toast.makeText(this, R.string.gps_off,
//                        Toast.LENGTH_LONG).show();
//                break;
//            case 3:
//                String text = getResources().getString(R.string.offline_mode) + "/" + getResources().getString(R.string.gps_off);;
//                Toast.makeText(this, text,
//                        Toast.LENGTH_LONG).show();
//                break;
//            default: break;
//
//        }
//    }
//
//    public void createNetErrorDialog() {
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setMessage("You need a network connection to perform this action. Please turn on mobile network or Wi-Fi in Settings.")
//                .setTitle("Unable to connect")
//                .setCancelable(false)
//                .setPositiveButton("Settings",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//                                Intent i = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
//                                startActivity(i);
//                            }
//                        }
//                )
//                .setNegativeButton("Cancel",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//                                displayToast(1);
//                            }
//                        }
//                );
//        AlertDialog alert = builder.create();
//        alert.show();
//    }
//
//    public void createGpsErrorDialog() {
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setMessage("You need a location enabled to perform this action. Please turn on location in Settings.")
//                .setTitle("Unable to localize")
//                .setCancelable(false)
//                .setPositiveButton("Settings",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//                                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                                startActivity(i);
//                            }
//                        }
//                )
//                .setNegativeButton("Cancel",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//                                displayToast(2);
//                            }
//                        }
//                );
//        AlertDialog alert = builder.create();
//        alert.show();
//    }


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
}
