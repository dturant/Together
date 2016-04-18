package com.example.dagna.together;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dagna.together.helpers.EventAdapter;
import com.example.dagna.together.helpers.Events;
import com.example.dagna.together.onlineDatabase.DisplayEvents;
import com.example.dagna.together.onlineDatabase.GetUserByLogin;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    String json_string;
    JSONObject jsonObject;
    JSONArray jsonArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


    }

    public void login(View view) {
        Log.d("login", "tu jestem");
        String l = "d";
        String p = "t";
        final EditText login = (EditText) findViewById(R.id.login_login);
        final EditText password = (EditText) findViewById(R.id.login_password);
        final TextView error = (TextView) findViewById(R.id.login_error);

    /*    if (login.getText().toString().trim().equals("") || password.getText().toString().trim().equals("")) {
            error.setVisibility(View.VISIBLE);
            error.setText("All fields are required");
        } else if (login.getText().toString().equals(l) && password.getText().toString().equals(p)) {
            addToPreferences(login.getText().toString());
            Intent intent = new Intent(this, TimelineActivity.class);
            startActivity(intent);
            //tutaj odpytanie servicu np
        } else {
            error.setVisibility(View.VISIBLE);
            error.setText("Login failed. Check your login and password.");
        } */


        GetUserByLogin getUserByLogin = (GetUserByLogin) new GetUserByLogin(new GetUserByLogin.AsyncResponse() {

            @Override
            public void processFinish(String output) {
                Log.d("process finish!!!!",GetUserByLogin.json_string);
                if (GetUserByLogin.json_string == null) {
                    Log.d("fail!!!!", "fail :(");
                    //Toast.makeText(getApplicationContext(), "first get json", Toast.LENGTH_LONG).show();
                    error.setVisibility(View.VISIBLE);
                    error.setText("All fields are required");
                } else {
                    Log.d("data!!!!!!!", GetUserByLogin.json_string);
                    json_string = GetUserByLogin.json_string;

                    try {
                        Log.d("login", login.getText().toString());
                        jsonObject = new JSONObject(json_string);
                        jsonArray=jsonObject.getJSONArray("server_response");
                        JSONObject JO = jsonArray.getJSONObject(0);

                        String db_id, db_description, db_city, db_grade, db_password, db_login;

                        db_id = JO.getString("user_id");
                        db_password = JO.getString("password");
                        db_description = JO.getString("description");
                        db_city = JO.getString("city");
                      //  db_id = JO.getString("id");

                        if(password.getText().toString().equals(db_password)){
                            addToPreferences(login.getText().toString());
                            Intent intent = new Intent(getApplicationContext(), TimelineActivity.class);
                            startActivity(intent);
                        }
                        else{
                            error.setVisibility(View.VISIBLE);
                            error.setText("Login failed. Check your login and password.");
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).execute(login.getText().toString());
    }



    private void addToPreferences(String login){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("login",login);
        editor.apply();
    }

}
