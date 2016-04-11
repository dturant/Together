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

import com.example.dagna.together.services.DatabaseIntentService;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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
