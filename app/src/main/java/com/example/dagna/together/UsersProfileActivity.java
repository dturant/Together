package com.example.dagna.together;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UsersProfileActivity extends AppCompatActivity {
    String json_string, user_id;
    JSONObject jsonObject;
    JSONArray jsonArray;

    Context context;

    TextView Login, City, Note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        user_id=getIntent().getExtras().getString("user_id");
        context = getApplicationContext();

        Login = (TextView)findViewById(R.id.users_profile_login);
        City = (TextView)findViewById(R.id.users_profile_city);
        Note = (TextView)findViewById(R.id.users_profile_note);

        json_string=getIntent().getExtras().getString("json_data");

        Log.d("USERS ID JSON", json_string);
        try {
            // Log.d("login", login);
            jsonObject = new JSONObject(json_string);
            jsonArray = jsonObject.getJSONArray("server_response");
            JSONObject JO = jsonArray.getJSONObject(0);

            String db_login, db_city, db_rate;

            db_login = JO.getString("login");
            db_city = JO.getString("city");

            getSupportActionBar().setTitle(db_login + "'s profile");

            City.append(db_city);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
