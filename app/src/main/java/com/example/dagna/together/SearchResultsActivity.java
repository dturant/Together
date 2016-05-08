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
import com.example.dagna.together.helpers.GeneralHelpers;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        eventsList.clear();
        listView = (ListView)findViewById(R.id.resultsListView);
        eventAdapter=new EventAdapter(this, R.layout.content_search_results);
        listView.setAdapter(eventAdapter);
        json_string=getIntent().getExtras().getString("json_data");
        final Context context = this;

        try {
            jsonObject=new JSONObject(json_string);
            jsonArray=jsonObject.getJSONArray("server_response");

            int count=0;
            String name, description,id, city,category_id;

            while(count<jsonArray.length()){
                JSONObject JO = jsonArray.getJSONObject(count);
                name=JO.getString("name");
                description=JO.getString("description");
                id=JO.getString("event_id");
                city=JO.getString("city");
                category_id=JO.getString("category_id");
                Integer image;
                Log.d("category_id", category_id);
                if(category_id.equals("2"))
                {
                    image=R.drawable.blue_stone;
                }
                else if(category_id.equals("3")){
                    image=R.drawable.green_stone;
                }
                else if(category_id.equals("4")){
                    image=R.drawable.yellow_stone;
                }
                else{
                    image=R.drawable.red_stone;
                }
                Events events=new Events(id,name, description,image);
                eventAdapter.add(events);
                eventsList.add(events);
                count++;


            }

            listView.setClickable(true);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView parentView, View childView,
                                        int position, long id) {
                    if(GeneralHelpers.isNetworkAvailable((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE)))
                    {
                        String eventId=eventsList.get(position).getId();
                        Log.d("eventid", eventId);
                        displayEvent(eventId);
                    }
                    else
                    {
                         GeneralHelpers.createNetErrorDialog(context);
                    }
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
//        if (id == R.id.action_settings) {
//            return true;
//        }

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
            if(GeneralHelpers.isNetworkAvailable((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE)))
            {
                Intent intent = new Intent(this, AddEventActivity.class);
                startActivity(intent);
                return true;
            }
            else
            {
                GeneralHelpers.createNetErrorDialog(this);
            }
        }

        if (id == R.id.search) {
            if(GeneralHelpers.isNetworkAvailable((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE)))
            {
                Intent intent = new Intent(this, SearchActivity.class);
                startActivity(intent);
                return true;
            }
            else
            {
                GeneralHelpers.createNetErrorDialog(this);
            }
        }

        return super.onOptionsItemSelected(item);
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
                    startActivity(intent);

                }
            }
        }).execute(eventId);


    }

}
