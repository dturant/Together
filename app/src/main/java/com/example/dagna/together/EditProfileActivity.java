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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dagna.together.helpers.GeneralHelpers;

public class EditProfileActivity extends AppCompatActivity {

    EditText Description, City, Login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Login = (EditText) findViewById(R.id.edit_login);
        City = (EditText) findViewById(R.id.edit_city);
        Description = (EditText) findViewById(R.id.edit_user_description);
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


    public void edit(View view)
    {
        if(GeneralHelpers.isNetworkAvailable((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE)))
        {
            String description, city, login;
            description = Description.getText().toString();
            city = City.getText().toString();
            login = Login.getText().toString();
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        }
        else
        {
            GeneralHelpers.createNetErrorDialog(this);
        }
    }


}
