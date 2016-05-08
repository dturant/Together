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
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.dagna.together.helpers.EventAdapter;
import com.example.dagna.together.helpers.Events;
import com.example.dagna.together.helpers.GeneralHelpers;
import com.example.dagna.together.helpers.UserAdapter;
import com.example.dagna.together.helpers.Users;
import com.example.dagna.together.helpers.ViewPagerAdapter;
import com.example.dagna.together.onlineDatabase.AddUser;
import com.example.dagna.together.onlineDatabase.DisplayEvents;
import com.example.dagna.together.onlineDatabase.GetEventById;
import com.example.dagna.together.onlineDatabase.GetSubscribedUsers;
import com.example.dagna.together.onlineDatabase.GetUserById;
import com.example.dagna.together.onlineDatabase.GetUserByLogin;
import com.example.dagna.together.onlineDatabase.JoinEvent;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class EventActivity extends AppCompatActivity {
    String json_string, event_id, user_id, json_string_event;
    JSONObject jsonObject;
    JSONArray jsonArray;

    Context context;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;

    static ArrayList<Users> usersList = new ArrayList<>();

    public static boolean userSigned=false;

    public static ArrayList<Users> getUsersList()
    {
        return usersList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        user_id = preferences.getString("id", "");

        setContentView(R.layout.activity_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        json_string_event=getIntent().getExtras().getString("json_data");
        event_id=getIntent().getExtras().getString("event_id");
        context = this;
        getUsers();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
            startActivity(intent);;
            return true;
        }

        if (id == R.id.profile) {
            getUsersData();
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

    private void getUsersData(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String loginFromPref = preferences.getString("login", "");
        final String login = loginFromPref;
        GetUserByLogin getUserByLogin = (GetUserByLogin) new GetUserByLogin(new GetUserByLogin.AsyncResponse() {

            @Override
            public void processFinish(String output) {
                if (GetUserByLogin.json_string.length() < 30) {

                } else {
                    json_string = GetUserByLogin.json_string;

                    Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                    intent.putExtra("json_data", json_string);
                    startActivity(intent);


                }
            }
        }).execute(login);
    }

    private void updateUsers(){
        usersList.clear();
        final Context context = this;

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


                            Users users = new Users(id, login);
                            usersList.add(users);
                            idList.add(id);
                            count++;

                        }
                        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), event_id, context, json_string_event, (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE));
                        viewPager.setAdapter(viewPagerAdapter);



                        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

                        tabLayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).execute(event_id);
    }

    private void getUsers(){
       usersList.clear();
        final Context context = this;

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

                            Users users = new Users(id, login);
                            usersList.add(users);
                            idList.add(id);
                            count++;

                        }
                        //list got so display
                        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), event_id, context, json_string_event, (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE));
                        viewPager.setAdapter(viewPagerAdapter);

                        final TabLayout.Tab information = tabLayout.newTab();
                        final TabLayout.Tab participants = tabLayout.newTab();

                        information.setText(R.string.information);
                        participants.setText(R.string.participants);

                        tabLayout.addTab(information, 0);
                        tabLayout.addTab(participants, 1);

                        tabLayout.setTabTextColors(ContextCompat.getColorStateList(context, R.color.tab_selector));
                        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(context, R.color.indicator));

                        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

                        tabLayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).execute(event_id);


    }

    public void joinEvent(View view)
    {
        if(GeneralHelpers.isNetworkAvailable((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE)))
        {
            Log.d("event_id", event_id);
            Log.d("user_id", user_id);
            JoinEvent joinEvent = (JoinEvent) new JoinEvent(new JoinEvent.AsyncResponse() {

                @Override
                public void processFinish(String output) {

                    Log.d("OUTPUT", output);
                    if (output.equals("error")) {
                        Log.d("ERROR", "ERROR");

                    } else {
                        InformationTabFragment.changeButton(true);
                        updateUsers();
                        Toast.makeText(context, R.string.signed, Toast.LENGTH_LONG).show();
                    }
                }
            }).execute(user_id, event_id);
        }
        else
        {
            GeneralHelpers.createNetErrorDialog(context);
        }
    }

    public void showOnMap(View view){

        try {
            jsonObject = new JSONObject(json_string_event);
            jsonArray = jsonObject.getJSONArray("server_response");
            JSONObject JO = jsonArray.getJSONObject(0);

            String name, street_name, street_number, city, country;

            name = JO.getString("name");
            street_name=JO.getString("street_name");
            street_number=JO.getString("street_number");
            city=JO.getString("city");
            country=JO.getString("country");

            Intent intent = new Intent(this, MapActivity.class);
            intent.putExtra("address", city+ ", " + street_name + " " + street_number);
            intent.putExtra("name", name);
            startActivity(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }



    }
}
