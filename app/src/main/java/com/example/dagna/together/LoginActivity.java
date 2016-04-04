package com.example.dagna.together;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


    }

    public void login(View view){
        String l="d";
        String p="t";
        EditText login = (EditText) findViewById(R.id.login_login);
        EditText password = (EditText) findViewById(R.id.login_password);
        TextView error = (TextView) findViewById(R.id.login_error);

        if( login.getText().toString().trim().equals("") || password.getText().toString().trim().equals("")){
            error.setVisibility(View.VISIBLE);
            error.setText("All fields are required");
        }

        else if(login.getText().toString().equals(l) && password.getText().toString().equals(p)){
            Intent intent = new Intent(this, TimelineActivity.class);
            startActivity(intent);
        }
        else{
            error.setVisibility(View.VISIBLE);
            error.setText("Login failed. Check your login and password.");
        }

    }

}
