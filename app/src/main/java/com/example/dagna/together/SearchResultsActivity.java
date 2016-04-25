package com.example.dagna.together;

import android.content.Context;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.content.SharedPreferences;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.dagna.together.helpers.EventAdapter;
import com.example.dagna.together.helpers.Events;
import com.example.dagna.together.onlineDatabase.GetEventById;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchResultsActivity extends AppCompatActivity {

    String json_string;
    JSONObject jsonObject;
    JSONArray jsonArray;
    EventAdapter eventAdapter;
    ListView listView;
    static ArrayList<Events> eventsList = new ArrayList<>();

    public static ArrayList<Events> getEventsList()
    {
        return eventsList;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void displayToast()
    {
        Toast.makeText(this, R.string.offline_mode,
                Toast.LENGTH_LONG).show();
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
                                displayToast();
                            }
                        }
                );
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //wziac cursor z bazki
      //  Cursor c = DatabaseIntentService.getCursor();
      //  updateList(c);

        eventsList.clear();
        listView = (ListView)findViewById(R.id.resultsListView);
        eventAdapter=new EventAdapter(this, R.layout.content_search_results);
        listView.setAdapter(eventAdapter);
        json_string=getIntent().getExtras().getString("json_data");

        try {
            jsonObject=new JSONObject(json_string);
            jsonArray=jsonObject.getJSONArray("server_response");

            int count=0;
            String name, description,id, city;

            while(count<jsonArray.length()){
                JSONObject JO = jsonArray.getJSONObject(count);
                name=JO.getString("name");
                description=JO.getString("description");
                id=JO.getString("event_id");
                city=JO.getString("city");
                Events events=new Events(id,name, description);
                eventAdapter.add(events);
                eventsList.add(events);
                count++;


            }

            listView.setClickable(true);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView parentView, View childView,
                                        int position, long id) {
                    String eventId=eventsList.get(position).getId();
                    Log.d("eventid", eventId);
                    displayEvent(eventId);

                }

                public void onNothingSelected(AdapterView parentView) {

                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

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
            Intent intent = new Intent(this, RegisterOrLoginActivity.class);
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

  /*  private void updateList(Cursor cursor)
    {
        String[] fromColumns = {DatabaseHelper.KEY_EVENT_NAME, DatabaseHelper.KEY_DSCRP};
        int[] toViews = {R.id.title, R.id.description};
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.content_event_list, cursor, fromColumns, toViews, 0);

        ListView listView = (ListView) findViewById( R.id.resultsListView );
        listView.setAdapter(adapter);

        listView.setClickable(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parentView, View childView,
                                    int position, long id) {
                displayEvent(position);

            }

            public void onNothingSelected(AdapterView parentView) {

            }
        });
    } */

    private void displayEvent(String eventId)
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
                    startActivity(intent);

                }
            }
        }).execute(eventId);


    }

}
