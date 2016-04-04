package com.example.dagna.together.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import com.example.dagna.together.helpers.DatabaseHelper;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class DatabaseIntentService extends IntentService {
    //na razie wysylam wszystkim na raz, moze potem bede rozdzielal
    public static final String NOTIFICATION = "com.example.dagna.together";
    public static final String RESULT_OK = "success";
    public static final String RESULT_FAIL = "failure";
    public static final String RESULT = "result";

    //values send via intent informing what action to perform
    public static final String ACTION = "action";
    public static final String GET_EVENTS_FROM_USER_CITY = "get events";
    public static final String CHECK_CREDENTIALS = "check credentials";

    //Helping data
    public static final String CITY = "city";

    private static Cursor cursor;
    private int lastResult;//???


    public DatabaseIntentService() {
        super("DatabaseIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            String action = bundle.getString(ACTION);
            switch(action)
            {
                case GET_EVENTS_FROM_USER_CITY:
                {
                    String city = bundle.getString(CITY);
                    DatabaseHelper db;
                    db = new DatabaseHelper(getApplicationContext());
                    cursor = db.getEventsByCity(city);
                    publishResults(RESULT_OK);
                    break;
                }
                default:
                {
                    publishResults(RESULT_FAIL);
                }

            }
        }

        publishResults(RESULT_FAIL);

    }

    private void publishResults(String result) {
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra(RESULT, result);
        sendBroadcast(intent);
    }

    public static Cursor getCursor()
    {
        return cursor;
    }
}
