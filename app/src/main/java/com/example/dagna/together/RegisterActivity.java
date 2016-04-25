package com.example.dagna.together;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dagna.together.onlineDatabase.*;

public class RegisterActivity extends AppCompatActivity {
    EditText Login, Password, City, PasswordConfirmation;
    TextView Error;
    String login, password, city, passwordConfirmation, error;

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
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Login=(EditText) findViewById(R.id.register_login);
        Password=(EditText) findViewById(R.id.register_password);
        PasswordConfirmation=(EditText) findViewById(R.id.register_passwordConfirmation);
        City=(EditText) findViewById(R.id.register_city);
        Error=(TextView) findViewById(R.id.register_error);


    }

    public void register(View view){

        if(isNetworkAvailable())
        {
            login=Login.getText().toString();
            password=Password.getText().toString();
            passwordConfirmation=PasswordConfirmation.getText().toString();
            city=City.getText().toString();

            if( login.trim().equals("") || password.trim().equals("")
                    || passwordConfirmation.trim().equals("") || city.trim().equals("")  ){
                Error.setVisibility(View.VISIBLE);
                Error.setText("All fields are required");
            }

            else if(!password.equals(passwordConfirmation)){
                Error.setVisibility(View.VISIBLE);
                Error.setText("Passwords don't match.");
                return;
            }
            else {
                AddUser addUser = (AddUser) new AddUser(new AddUser.AsyncResponse() {

                    @Override
                    public void processFinish(String output) {

                        Log.d("OUTPUT", output);
                        if (output.equals("error")) {
                            Log.d("ERROR", "ERROR");
                            Error.setVisibility(View.VISIBLE);
                            Error.setText("This login is already taken. Try another one.");

                        } else {
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(intent);
                        }
                    }
                }).execute(login, password, city);

            }
        }
        else
        {
            createNetErrorDialog();
        }
    }

}
