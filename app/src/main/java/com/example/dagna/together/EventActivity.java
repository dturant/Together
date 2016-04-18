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

    TextView Name;

    //trzymac tu gdzies id, ma przyjsc w intencie
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Name = (TextView)findViewById(R.id.event_nameTextView);

        json_string=getIntent().getExtras().getString("json_data");
        try {
            // Log.d("login", login);
            jsonObject = new JSONObject(json_string);
            jsonArray = jsonObject.getJSONArray("server_response");
            JSONObject JO = jsonArray.getJSONObject(0);

            String db_id, db_name, db_description, db_category_id, db_user_id, db_street_name, db_street_number, db_city, db_zipcode, db_country;

            db_id = JO.getString("event_id");
            db_name = JO.getString("name");
            db_description = JO.getString("description");
            db_category_id=JO.getString("category_id");
            db_user_id = JO.getString("user_id");
            db_street_name=JO.getString("street_name");
            db_street_number=JO.getString("street_number");
            db_city=JO.getString("city");
            db_zipcode=JO.getString("zipcode");
            db_country=JO.getString("country");

            Name.setText(db_name);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
