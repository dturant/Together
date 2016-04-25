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
import com.example.dagna.together.onlineDatabase.DisplayEvents;
import com.example.dagna.together.onlineDatabase.GetUserByLogin;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    String json_string;
    JSONObject jsonObject;
    JSONArray jsonArray;

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void displayToast()
    {
        Toast.makeText(this, R.string.offline_mode,
                Toast.LENGTH_LONG).show();
    }

    protected void createNetErrorDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("You need a network connection to perform this action. Please turn on mobile network or Wi-Fi in Settings.")
                .setTitle("Unable to connect")
                .setCancelable(false)
                .setPositiveButton("Settings",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent i = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                                startActivity(i);
                            }
                        }
                )
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                displayToast();
                            }
                        }
                );
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


    }

    public void login(View view) {
        final EditText Login = (EditText) findViewById(R.id.login_login);
        final EditText Password = (EditText) findViewById(R.id.login_password);
        final TextView Error = (TextView) findViewById(R.id.login_error);

        final String login, password;
        login = Login.getText().toString();
        password = Password.getText().toString();

        if(isNetworkAvailable())
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

                                String db_id, db_description, db_city, db_grade, db_password, db_login;

                                db_id = JO.getString("user_id");
                                db_password = JO.getString("password");
                                db_description = JO.getString("description");
                                db_city = JO.getString("city");

                                if (password.equals(db_password)) {
                                    addToPreferences(login);
                                    Intent intent = new Intent(getApplicationContext(), TimelineActivity.class);
                                    startActivity(intent);
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
            createNetErrorDialog();
        }
    }



    private void addToPreferences(String login){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("login",login);
        editor.apply();
    }

}
