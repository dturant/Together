package com.example.dagna.together;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.dagna.together.onlineDatabase.AddEvent;
import com.example.dagna.together.onlineDatabase.AddUser;
import com.example.dagna.together.onlineDatabase.GetCategories;

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
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void addEvent(View view)
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



}
