package com.example.dagna.together;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dagna.together.helpers.DatabaseHelper;
import com.example.dagna.together.helpers.EventAdapter;
import com.example.dagna.together.helpers.Events;
import com.example.dagna.together.onlineDatabase.DisplayEvents;
import com.example.dagna.together.onlineDatabase.GetEventById;
import com.example.dagna.together.services.DatabaseIntentService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TimelineActivity extends AppCompatActivity {

    String json_string;
    JSONObject jsonObject;
    JSONArray jsonArray;
    EventAdapter eventAdapter;
    ListView listView;
    static ArrayList<Events> eventsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().getBooleanExtra("EXIT", false)) {
            Intent intent = new Intent(this, RegisterOrLoginActivity.class);
            startActivity(intent);
            finish();
        }
        else
        {
            setContentView(R.layout.activity_timeline);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            String login = preferences.getString("login", "");

            if(login.equals("")){
                Intent intent = new Intent(this, RegisterOrLoginActivity.class);
                startActivity(intent);
                finish();
            }
            else
            {
                if(isNetworkAvailable())
                {
                    //getEventsFromOnlineDB();
                }
                else
                {
//                    Toast.makeText(this, R.string.offline_mode,
//                            Toast.LENGTH_LONG).show();
                    //getEventsFromLocalDB();
                }
            }
        }
    }



    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static ArrayList<Events> getEventsList()
    {
        return eventsList;
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

    private void getEventsFromLocalDB(String city)
    {
//            //TODO na razie trzeba setowac DB za kazdym razem zeby dzialalo
//            DatabaseHelper db;
//            db = new DatabaseHelper(getApplicationContext());
//            db.setupDatabase();

            Intent intent = new Intent(this, DatabaseIntentService.class);
            intent.putExtra(DatabaseIntentService.ACTION, DatabaseIntentService.GET_EVENTS_FROM_USER_CITY);
            //TODO: po miescie usera lub po wpisanym.
            intent.putExtra(DatabaseIntentService.CITY, city);
            startService(intent);
    }

    private void saveEventsToLocalDB()
    {
        Intent intent = new Intent(this, DatabaseIntentService.class);
        intent.putExtra(DatabaseIntentService.ACTION, DatabaseIntentService.SAVE_EVENTS_FROM_ONLINE_DB);
        startService(intent);
    }

    private void getEventsFromOnlineDB(String city)
    {
        eventsList.clear();
        listView = (ListView)findViewById(R.id.timelineListView);
        eventAdapter=new EventAdapter(this, R.layout.content_event_list);
        listView.setAdapter(eventAdapter);

        DisplayEvents displayEvents = (DisplayEvents) new DisplayEvents(new DisplayEvents.AsyncResponse() {

            @Override
            public void processFinish(String output) {
                Log.d("output",output);
                if(DisplayEvents.json_string==null){
                    Toast.makeText(getApplicationContext(), "first get json", Toast.LENGTH_LONG).show();
                }
                else{
                    //Intent intent = new Intent(getApplicationContext(), DisplayListView.class);
                    //intent.putExtra("json_data", DisplayEvents.json_string);
                    //startActivity(intent);

                    json_string=DisplayEvents.json_string;
                    // json_string=getIntent().getExtras().getString("json_data");
                    try {
                        jsonObject=new JSONObject(json_string);
                        jsonArray=jsonObject.getJSONArray("server_response");

                        int count=0;
                        String name, description, city,id;

                        while(count<jsonArray.length()){
                            JSONObject JO = jsonArray.getJSONObject(count);
                            id=JO.getString("id");
                            name=JO.getString("name");
                            description=JO.getString("description");
                            city=JO.getString("city");
                            Events events=new Events(id,name, description,city);
                            eventAdapter.add(events);
                            eventsList.add(events);
                            count++;
                        }
                        saveEventsToLocalDB();

                        listView.setClickable(true);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            public void onItemClick(AdapterView parentView, View childView,
                                                    int position, long id) {
                                if(isNetworkAvailable())
                                {
                                    String eventId=eventsList.get(position).getId();
                                    Log.d("eventid", eventId);
                                    displayEvent(eventId);
                                }
                                else
                                {
                                    createNetErrorDialog();
                                }
                            }

                            public void onNothingSelected(AdapterView parentView) {

                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).execute();
    }

  private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {

                String result = bundle.getString(DatabaseIntentService.RESULT);
                if(result.equals( DatabaseIntentService.RESULT_FAIL))
                {
                    //fail
                }
                else {
                    String s = "";
                    Cursor c = DatabaseIntentService.getCursor();
                    updateList(c);
                }
            }
        }
    };

    private void updateList(Cursor cursor)
    {
        String[] fromColumns = {DatabaseHelper.KEY_EVENT_NAME, DatabaseHelper.KEY_DSCRP};
        int[] toViews = {R.id.title, R.id.description};
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.content_event_list, cursor, fromColumns, toViews, 0);

        ListView listView = (ListView) findViewById( R.id.timelineListView );
        listView.setAdapter(adapter);

        listView.setClickable(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parentView, View childView,
                                    int position, long id) {
                if(isNetworkAvailable())
                {
                    TextView c = (TextView) childView.findViewById(R.id.title);
                    String eventId=eventsList.get(position).getId();
                    displayEvent(eventId);
                }
                else
                {
                    //TODO popup
                }
            }

            public void onNothingSelected(AdapterView parentView) {

            }
        });
    }
    private void displayEvent(final String eventId)
    {
        GetEventById getEventById = (GetEventById) new GetEventById(new GetEventById.AsyncResponse() {

            @Override
            public void processFinish(String output) {

                if (GetEventById.json_string.length() < 30) {
                    Log.d("fail!!!!", "fail :(");

                } else {
                    //Log.d("data!!!!!!!", GetUserByLogin.json_string);
                    json_string = GetEventById.json_string;
                    Intent intent = new Intent(getApplicationContext(), EventActivity.class);
                    intent.putExtra("json_data", json_string);
                    intent.putExtra("event_id", eventId);
                    Log.d("event_id from timeline", eventId);
                    startActivity(intent);

                }
            }
        }).execute(eventId);


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.logout) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            preferences.edit().remove("login").commit();
            preferences.edit().remove("id").commit();
            Intent intent = new Intent(this, TimelineActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("EXIT", true);
            startActivity(intent);
            return true;
        }

        if (id == R.id.profile) {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.add_event) {
            if(isNetworkAvailable())
            {
                Intent intent = new Intent(this, AddEventActivity.class);
                startActivity(intent);
                return true;
            }
            else
            {
                createNetErrorDialog();
            }
        }

        if (id == R.id.search) {
            if(isNetworkAvailable())
            {
                Intent intent = new Intent(this, SearchActivity.class);
                startActivity(intent);
                return true;
            }
            else
            {
                createNetErrorDialog();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String login = preferences.getString("login", "");

        registerReceiver(receiver, new IntentFilter(DatabaseIntentService.NOTIFICATION));

        if(login.equals("")){
            Intent intent = new Intent(this, RegisterOrLoginActivity.class);
            startActivity(intent);
            finish();
        }
        else
        {
            if(isNetworkAvailable())
            {
                getEventsFromOnlineDB("Dornbirn");
            }
            else
            {
                getEventsFromLocalDB("Dornbirn");
            }
        }
    }
}
