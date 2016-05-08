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
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dagna.together.helpers.EventAdapter;
import com.example.dagna.together.helpers.Events;
import com.example.dagna.together.helpers.GeneralHelpers;
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


        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        setContentView(R.layout.activity_login);


    }

    public void login(View view) {
        final EditText Login = (EditText) findViewById(R.id.login_login);
        final EditText Password = (EditText) findViewById(R.id.login_password);
        final TextView Error = (TextView) findViewById(R.id.login_error);

        final String login, password;
        login = Login.getText().toString();
        password = Password.getText().toString();

        if(GeneralHelpers.isNetworkAvailable((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE)))
        {
            if (login.trim().equals("") || password.trim().equals("")) {
                Error.setVisibility(View.VISIBLE);
                Error.setText("All fields are required");
            } else {
                GetUserByLogin getUserByLogin = (GetUserByLogin) new GetUserByLogin(new GetUserByLogin.AsyncResponse() {

                    @Override
                    public void processFinish(String output) {
                        Log.d("login", login);
                        Log.d("process finish!!!!", GetUserByLogin.json_string);
                        if (GetUserByLogin.json_string.length() < 30) {
                            Log.d("fail!!!!", "fail :(");
                            Error.setVisibility(View.VISIBLE);
                            Error.setText("Login failed. Check your login and password.");
                        } else {
                            Log.d("data!!!!!!!", GetUserByLogin.json_string);
                            json_string = GetUserByLogin.json_string;

                            try {
                                Log.d("login", login);
                                jsonObject = new JSONObject(json_string);
                                jsonArray = jsonObject.getJSONArray("server_response");
                                JSONObject JO = jsonArray.getJSONObject(0);

                                String id, db_description, db_city, db_grade, db_password, db_login;

                                id = JO.getString("user_id");
                                db_password = JO.getString("password");
                                db_description = JO.getString("description");
                                db_city = JO.getString("city");

                                if (password.equals(db_password)) {
                                    addLoginToPreferences(login);
                                    addIdToPreferences(id);

                                    Intent intent = new Intent(getApplicationContext(), TimelineActivity.class);
                                    startActivity(intent);
                                    RegisterOrLoginActivity.h.sendEmptyMessage(0);
                                    finish();
                                } else {
                                    Error.setVisibility(View.VISIBLE);
                                    Error.setText("Login failed. Check your password.");
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).execute(login);
            }
        }
        else
        {
            GeneralHelpers.createNetErrorDialog(this);
        }
    }

    private void addLoginToPreferences(String login){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("login",login);
        editor.apply();
    }

    private void addIdToPreferences(String id){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("id",id);
        editor.apply();
    }

}
