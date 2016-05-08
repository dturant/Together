package com.example.dagna.together;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.dagna.together.helpers.GeneralHelpers;
import com.example.dagna.together.onlineDatabase.AddEvent;
import com.example.dagna.together.onlineDatabase.AddUser;
import com.example.dagna.together.onlineDatabase.GetCategories;
import com.example.dagna.together.onlineDatabase.GetUserByLogin;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AddEventActivity extends AppCompatActivity {

    EditText Name, Description, Country, City, Street;
    Spinner Category;
    String json_string;
    JSONObject jsonObject;
    JSONArray jsonArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Category = (Spinner) findViewById(R.id.add_event_category);
        Name = (EditText) findViewById(R.id.add_event_name);
        Country = (EditText) findViewById(R.id.add_event_country);
        Description = (EditText) findViewById(R.id.add_event_description);
        City = (EditText) findViewById(R.id.add_event_city);
        Street = (EditText) findViewById(R.id.add_event_street);

        this.getCategories();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_event, menu);
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
            getUsersData();
            return true;
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
                Log.d("USERS ID OUTPUT", output);
                if (GetUserByLogin.json_string.length() < 30) {
                    Log.d("fail!!!!", "fail :(");

                } else {
                    json_string = GetUserByLogin.json_string;

                    Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                    intent.putExtra("json_data", json_string);
                    startActivity(intent);


                }
            }
        }).execute(login);
    }

    public void addEvent(View view)
    {
        if(GeneralHelpers.isNetworkAvailable((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE)))
        {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            String name, city, country, category, street, description,user;
            name = Name.getText().toString();
            city = City.getText().toString();
            country = Country.getText().toString();
            street = Country.getText().toString();
            description = Description.getText().toString();
            category = Category.getSelectedItem().toString();
            user=preferences.getString("login", "");
            //logika dodawania eventu i cos w intencie przekazac zeby byl prompt added!
            Log.d("category", category);
            Log.d("user", user);
            AddEvent addEvent = (AddEvent) new AddEvent(new AddEvent.AsyncResponse() {

                @Override
                public void processFinish(String output) {

                    Log.d("OUTPUT", output);
                    if (output.equals("error")) {
                        Log.d("ERROR", "ERROR");
                        //Error.setVisibility(View.VISIBLE);
                        //Error.setText("This login is already taken. Try another one.");

                    } else {
                        Intent intent = new Intent(getApplicationContext(), TimelineActivity.class);
                        startActivity(intent);
                    }
                }
            }).execute(name, category, description, country,city,street,user);
        }
        else
        {
            GeneralHelpers.createNetErrorDialog(this);
        }
    }

    private void fillSpinner(List<String> list){
        Category = (Spinner) findViewById(R.id.add_event_category);

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


}
