package com.example.dagna.together;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dagna.together.helpers.DatabaseHelper;
import com.example.dagna.together.helpers.EventAdapter;
import com.example.dagna.together.helpers.Events;
import com.example.dagna.together.services.DatabaseIntentService;
import com.example.dagna.together.onlineDatabase.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TimelineActivity extends AppCompatActivity {

    String json_string;
    JSONObject jsonObject;
    JSONArray jsonArray;
    EventAdapter eventAdapter;
    ListView listView;

  /*  private BroadcastReceiver receiver = new BroadcastReceiver() {
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
                    String s = "";
                    Cursor c = DatabaseIntentService.getCursor();
                    updateList(c);
//                    if (c.moveToFirst()) {
//                        do {
//                            s+= c.getString(c.getColumnIndex("city"));
////                            Todo td = new Todo();
////                            td.setId(c.getInt((c.getColumnIndex(KEY_ID))));
////                            td.setNote((c.getString(c.getColumnIndex(KEY_TODO))));
////                            td.setCreatedAt(c.getString(c.getColumnIndex(KEY_CREATED_AT)));
////
////                            // adding to todo list
////                            todos.add(td);
//
//                        } while (c.moveToNext());
//                    }
//                    TextView a = (TextView) findViewById(R.id.timeline_test);
//                    a.setText(s);
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
                displayEvent(position);

            }

            public void onNothingSelected(AdapterView parentView) {

            }
        });
    }
*/
    private void displayEvent(int eventId)
    {
        Intent intent = new Intent(this, EventActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //StrictMode.enableDefaults();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String login = preferences.getString("login", "");


        if(login.equals("")){
            Intent intent = new Intent(this, RegisterOrLoginActivity.class);
            startActivity(intent);
            finish();
        }
        else
        {
         /*   //TODO na razie trzeba setowac DB za kazdym razem zeby dzialalo
            DatabaseHelper db;
            db = new DatabaseHelper(getApplicationContext());
            db.setupDatabase();

            Intent intent = new Intent(this, DatabaseIntentService.class);
            intent.putExtra(DatabaseIntentService.ACTION, DatabaseIntentService.GET_EVENTS_FROM_USER_CITY);
            //TODO: po user id
            intent.putExtra(DatabaseIntentService.CITY, "New York");
            startService(intent); */

            listView = (ListView)findViewById(R.id.timelineListView);
            eventAdapter=new EventAdapter(this, R.layout.content_event_list);
            listView.setAdapter(eventAdapter);

            DisplayEvents displayEvents = (DisplayEvents) new DisplayEvents(new DisplayEvents.AsyncResponse() {

                @Override
                public void processFinish(String output) {
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
                            String name, description;

                            while(count<jsonArray.length()){
                                JSONObject JO = jsonArray.getJSONObject(count);
                                name=JO.getString("name");
                                description=JO.getString("description");
                                Events events=new Events(name, description);
                                eventAdapter.add(events);
                                count++;


                            }
                            listView.setClickable(true);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                public void onItemClick(AdapterView parentView, View childView,
                                                        int position, long id) {
                                    displayEvent(position);

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
            Intent intent = new Intent(this, AddEventActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.search) {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        //unregisterReceiver(receiver);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        //registerReceiver(receiver, new IntentFilter(DatabaseIntentService.NOTIFICATION));
    }

}
