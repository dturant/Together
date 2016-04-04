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

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


    }

    public void register(View view){
        String takenLogin="d";

        EditText login = (EditText) findViewById(R.id.register_login);
        EditText password = (EditText) findViewById(R.id.register_password);
        EditText passwordConfirmation = (EditText) findViewById(R.id.register_passwordConfirmation);
        EditText city = (EditText) findViewById(R.id.register_city);
        TextView error = (TextView) findViewById(R.id.register_error);

        if( login.getText().toString().trim().equals("") || password.getText().toString().trim().equals("")
             || passwordConfirmation.getText().toString().trim().equals("") || city.getText().toString().trim().equals("")  ){
            error.setVisibility(View.VISIBLE);
            error.setText("All fields are required");
        }

        else if(login.getText().toString().equals(takenLogin)){
            error.setVisibility(View.VISIBLE);
            error.setText("This login is already taken. Try another one.");
        }
        else if(!password.getText().toString().equals(passwordConfirmation.getText().toString())){
            error.setVisibility(View.VISIBLE);
            error.setText("Passwords don't match.");
        }
        else{
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }




    }

}
