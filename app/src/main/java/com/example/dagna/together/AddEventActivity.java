package com.example.dagna.together;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class AddEventActivity extends AppCompatActivity {

    EditText Name, Description, Country, City, Street;
    Spinner Category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Category = (Spinner) findViewById(R.id.add_event_category);
        Name = (EditText) findViewById(R.id.add_event_name);
        Country = (EditText) findViewById(R.id.add_event_country);
        Description = (EditText) findViewById(R.id.add_event_description);
        City = (EditText) findViewById(R.id.add_event_city);
        Street = (EditText) findViewById(R.id.add_event_street);

        List<String> categories = new ArrayList<String>();
        categories.add("Automobile");
        categories.add("Business Services");
        categories.add("Computers");
        categories.add("Education");
        categories.add("Personal");
        categories.add("Travel");

        // Creating adapter for spinner
        Spinner categorySpinner = (Spinner) findViewById(R.id.add_event_category);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        categorySpinner.setAdapter(dataAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void addEvent(View view)
    {
        String name, city, country, category, street, description;
        name = Name.getText().toString();
        city = City.getText().toString();
        country = Country.getText().toString();
        street = Country.getText().toString();
        description = Description.getText().toString();
        category = Category.getSelectedItem().toString();
        //logika dodawania eventu i cos w intencie przekazac zeby byl prompt added!
        Intent intent = new Intent(this, TimelineActivity.class);
        startActivity(intent);
    }



}
