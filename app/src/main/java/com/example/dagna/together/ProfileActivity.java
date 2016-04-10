package com.example.dagna.together;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.dagna.together.services.DatabaseService;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String loginFromPref = preferences.getString("login", "");

        getSupportActionBar().setTitle(loginFromPref+"'s profile");

        TextView login = (TextView) findViewById(R.id.profile_login);
        TextView city = (TextView) findViewById(R.id.profile_city);
        TextView rate = (TextView) findViewById(R.id.profile_rate);

        login.append(loginFromPref);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (id == R.id.edit) {
            //Intent intent = new Intent(this, EditProfileActivity.class);
            //startActivity(intent);
            return true;
        }

        if (id == R.id.logout) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            preferences.edit().remove("login").commit();
            Intent intent = new Intent(this, RegisterOrLoginActivity.class);
            startActivity(intent);
            return true;
        }



        return super.onOptionsItemSelected(item);
    }

}
