package com.example.dagna.together;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

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

    public void edit(View view)
    {
        String description, city, login;
        description = Description.getText().toString();
        city = City.getText().toString();
        login = Login.getText().toString();
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }


}
