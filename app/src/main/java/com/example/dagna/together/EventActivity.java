package com.example.dagna.together;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import com.example.dagna.together.helpers.UserAdapter;
import com.example.dagna.together.helpers.Users;
import com.example.dagna.together.onlineDatabase.AddUser;
import com.example.dagna.together.onlineDatabase.DisplayEvents;
import com.example.dagna.together.onlineDatabase.GetSubscribedUsers;
import com.example.dagna.together.onlineDatabase.GetUserById;
import com.example.dagna.together.onlineDatabase.JoinEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class EventActivity extends AppCompatActivity {
    String json_string, event_id;
    JSONObject jsonObject;
    JSONArray jsonArray;

    Context context;

    TextView Name, Category, Description, Country, City, Street,User ;

    ListView listView;

    UserAdapter userAdapter;

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

    public void joinEvent(View view)
    {
        if(isNetworkAvailable())
        {
            //join event

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            String user_id = preferences.getString("id", "");
            Log.d("event_id", event_id);
            Log.d("user_id", user_id);
            JoinEvent joinEvent = (JoinEvent) new JoinEvent(new JoinEvent.AsyncResponse() {

                @Override
                public void processFinish(String output) {

                    Log.d("OUTPUT", output);
                    if (output.equals("error")) {
                        Log.d("ERROR", "ERROR");
                        //Error.setVisibility(View.VISIBLE);
                        //Error.setText("This login is already taken. Try another one.");

                    } else {
                        //Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        //startActivity(intent);
                        Toast.makeText(context,R.string.signed, Toast.LENGTH_LONG).show();
                    }
                }
            }).execute(user_id, event_id);
        }
        else
        {
            createNetErrorDialog();
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
                                displayToast();
                            }
                        }
                );
        AlertDialog alert = builder.create();
        alert.show();
    }

    //trzymac tu gdzies id, ma przyjsc w intencie
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        event_id=getIntent().getExtras().getString("event_id");
        context = getApplicationContext();

        Name = (TextView)findViewById(R.id.event_nameTextView);
        Category = (TextView)findViewById(R.id.event_categoryTextView);
        Description = (TextView)findViewById(R.id.event_descriptionTextView);
        Country = (TextView)findViewById(R.id.event_countryTextView);
        City = (TextView)findViewById(R.id.event_cityTextView);
        Street = (TextView)findViewById(R.id.event_streetTextView);
        User = (TextView)findViewById(R.id.event_userTextView);

        //listView = (ListView) findViewById(R.id.eventListView);
        //TODO if is internet
        json_string=getIntent().getExtras().getString("json_data");
        try {
            // Log.d("login", login);
            jsonObject = new JSONObject(json_string);
            jsonArray = jsonObject.getJSONArray("server_response");
            JSONObject JO = jsonArray.getJSONObject(0);

            String db_id, db_name, db_description, db_category, db_user, db_street_name, db_street_number, db_city, db_zipcode, db_country;

            db_id = JO.getString("event_id");
            db_name = JO.getString("name");
            db_description = JO.getString("description");
            db_category=JO.getString("category");
            db_user = JO.getString("user");
            db_street_name=JO.getString("street_name");
            db_street_number=JO.getString("street_number");
            db_city=JO.getString("city");
            db_zipcode=JO.getString("zipcode");
            db_country=JO.getString("country");

            getSupportActionBar().setTitle(db_name);

            Name.append(db_name);
            Category.append(db_category);
            Description.append(db_description);
            City.append(db_city);
            Country.append(db_country);
            Street.append(db_street_name);
            User.append(db_user);

            getUsers();

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void getUsers(){
        listView = (ListView)findViewById(R.id.eventListView);
        userAdapter=new UserAdapter(this, R.layout.content_user_list);
        listView.setAdapter(userAdapter);
       // userAdapter=new UserAdapter(this, R.layout.content_user_list);

        GetSubscribedUsers getSubscribedUsers = (GetSubscribedUsers) new GetSubscribedUsers(new GetSubscribedUsers.AsyncResponse() {

            @Override
            public void processFinish(String output) {
                Log.d(" getSubscribedUsers:",output);
                if(GetSubscribedUsers.json_string==null){
                    Toast.makeText(getApplicationContext(), "first get json", Toast.LENGTH_LONG).show();
                }
                else{
                    json_string=GetSubscribedUsers.json_string;

                    try {
                        jsonObject=new JSONObject(json_string);
                        jsonArray=jsonObject.getJSONArray("server_response");
                        List<String> idList=new ArrayList<String>();
                        int count=0;
                        String id, login;

                        while(count<jsonArray.length()){
                            JSONObject JO = jsonArray.getJSONObject(count);
                            id=JO.getString("id");
                           login=JO.getString("login");

                            //Events events=new Events(id,name, description,city);
                            //eventAdapter.add(events);
                            Users users = new Users(id, login);
                            userAdapter.add(users);
                            //eventsList.add(events);
                            idList.add(id);
                            count++;

                        }
                        Log.d("LISTA USEROW",idList.toString() );
                        //TODO dodac te glupia liste o listview czy cus
                        listView.setClickable(true);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            public void onItemClick(AdapterView parentView, View childView,
                                                    int position, long id) {
                                if(isNetworkAvailable())
                                {
                                    //String eventId=eventsList.get(position).getId();
                                   // Log.d("eventid", eventId);
                                   // displayEvent(eventId);
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
        }).execute(event_id);
    }

   /* private String getUserById(String id){
         String loginToReturn="";
        GetUserById getUserById = (GetUserById) new GetUserById(new GetUserById.AsyncResponse() {

            @Override
            public void processFinish(String output) {
                Log.d("output",output);
                if(DisplayEvents.json_string==null){
                    Toast.makeText(getApplicationContext(), "first get json", Toast.LENGTH_LONG).show();
                }
                else{

                    json_string=DisplayEvents.json_string;

                    try {
                        jsonObject=new JSONObject(json_string);
                        jsonArray=jsonObject.getJSONArray("server_response");

                        int count=0;
                        String login,id;

                        JSONObject JO = jsonArray.getJSONObject(count);
                        id=JO.getString("id");
                        login=JO.getString("login");

                        loginToReturn=login;
                        //Events events=new Events(id,name, description,city);
                       // eventAdapter.add(events);
                       //eventsList.add(events);

                        //saveEventsToLocalDB();


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).execute(id);
    } */

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
            startActivity(intent);;
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

}
