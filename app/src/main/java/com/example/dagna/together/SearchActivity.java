package com.example.dagna.together;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.dagna.together.services.DatabaseIntentService;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {

                String result = bundle.getString(DatabaseIntentService.RESULT);
                if(result == DatabaseIntentService.RESULT_FAIL)
                {
                    //fail
                }
                else {
                    goToResult();
                }
            }
        }
    };

    EditText Name, Country, City;
    Spinner Category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Name = (EditText) findViewById(R.id.search_event_name);
        Country = (EditText) findViewById(R.id.search_event_country);
        City = (EditText) findViewById(R.id.search_event_city);
        Category = (Spinner) findViewById(R.id.search_event_category);

        List<String> categories = new ArrayList<String>();
        categories.add("Automobile");
        categories.add("Business Services");
//        categories.add("Computers");
//        categories.add("Education");
//        categories.add("Personal");
//        categories.add("Travel");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        Category.setAdapter(dataAdapter);

    }

    @Override
    public void onPause()
    {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        registerReceiver(receiver, new IntentFilter(DatabaseIntentService.NOTIFICATION));
    }

    public void search(View view)
    {
        String name, city, country, category;
        name = Name.getText().toString();
        country = Country.getText().toString();
        city = City.getText().toString();
        category = Category.getSelectedItem().toString();

        Intent intent = new Intent(this, DatabaseIntentService.class);
        intent.putExtra(DatabaseIntentService.ACTION, DatabaseIntentService.GET_EVENTS_FROM_USER_CITY);
        //TODO: po user id
        intent.putExtra(DatabaseIntentService.CITY, "New York");
        startService(intent);
        //mozna jakies krecace kolko dodac
    }

    private void goToResult()
    {
        Intent intent = new Intent(this, SearchResultsActivity.class);
        startActivity(intent);
    }

}
