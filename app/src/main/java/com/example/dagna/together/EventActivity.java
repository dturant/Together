package com.example.dagna.together;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class EventActivity extends AppCompatActivity {
    String json_string;
    JSONObject jsonObject;
    JSONArray jsonArray;

    TextView Name, Category, Description, Country, City, Street,User ;

    //trzymac tu gdzies id, ma przyjsc w intencie
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Name = (TextView)findViewById(R.id.event_nameTextView);
        Category = (TextView)findViewById(R.id.event_categoryTextView);
        Description = (TextView)findViewById(R.id.event_descriptionTextView);
        Country = (TextView)findViewById(R.id.event_countryTextView);
        City = (TextView)findViewById(R.id.event_cityTextView);
        Street = (TextView)findViewById(R.id.event_streetTextView);
        User = (TextView)findViewById(R.id.event_userTextView);


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

            Name.append(db_name);
            Category.append(db_category);
            Description.append(db_description);
            City.append(db_city);
            Country.append(db_country);
            Street.append(db_street_name);
            User.append(db_user);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
