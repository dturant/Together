package com.example.dagna.together;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.example.dagna.together.helpers.DatabaseHelper;
import com.example.dagna.together.services.DatabaseIntentService;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TimelineActivity extends AppCompatActivity {

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
                    String s = "";
                    Cursor c = DatabaseIntentService.getCursor();
                    updateList(c);
//                    if (c.moveToFirst()) {
//                        do {
//                            s+= c.getString(c.getColumnIndex("city"));
////                            Todo td = new Todo();
////                            td.setId(c.getInt((c.getColumnIndex(KEY_ID))));
////                            td.setNote((c.getString(c.getColumnIndex(KEY_TODO))));
////                            td.setCreatedAt(c.getString(c.getColumnIndex(KEY_CREATED_AT)));
////
////                            // adding to todo list
////                            todos.add(td);
//
//                        } while (c.moveToNext());
//                    }
//                    TextView a = (TextView) findViewById(R.id.timeline_test);
//                    a.setText(s);
                }
            }
        }
    };

    private void updateList(Cursor cursor)
    {
        String[] fromColumns = {DatabaseHelper.KEY_EVENT_NAME, DatabaseHelper.KEY_DSCRP};
        int[] toViews = {R.id.title, R.id.description};
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.content_event_list, cursor, fromColumns, toViews, 0);

        ListView listView = (ListView) findViewById( R.id.timelineListView );
        listView.setAdapter(adapter);

        listView.setClickable(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parentView, View childView,
                                    int position, long id) {
                displayEvent(position);

            }

            public void onNothingSelected(AdapterView parentView) {

            }
        });
    }

    private void displayEvent(int eventId)
    {
        Intent intent = new Intent(this, EventActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        StrictMode.enableDefaults();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String login = preferences.getString("login", "");


        if(login.equals("")){
            Intent intent = new Intent(this, RegisterOrLoginActivity.class);
            startActivity(intent);
            finish();
        }
        else
        {
            //TODO na razie trzeba setowac DB za kazdym razem zeby dzialalo
            DatabaseHelper db;
            db = new DatabaseHelper(getApplicationContext());
            db.setupDatabase();

            Intent intent = new Intent(this, DatabaseIntentService.class);
            intent.putExtra(DatabaseIntentService.ACTION, DatabaseIntentService.GET_EVENTS_FROM_USER_CITY);
            //TODO: po user id
            intent.putExtra(DatabaseIntentService.CITY, "New York");
            startService(intent);



        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.logout) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            preferences.edit().remove("login").commit();
            Intent intent = new Intent(this, RegisterOrLoginActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.profile) {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.add_event) {
            Intent intent = new Intent(this, AddEventActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.search) {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
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


    public void register2(View view){

        new Thread() {
            @Override
            public void run() {
                //your code here


                try{
                    Class.forName("com.mysql.jdbc.Driver");
                    String url = "jdbc:mysql://db4free.net:3306/project_together";

                    String user = "together_mgdt";
                    String pwd = "ifejestfajne";

                    String dsa = "DSA";

//            String url = "jdbc:mysql://dbsrv.infeo.at:3306/fhv";
//
//            String user = "fhv";
//            String pwd = "datenmanagement";
                    Connection conn = null;

                    try{
            /* Initializing the connection */
                        conn = DriverManager.getConnection(url, user, pwd);

                        Statement stmt = conn.createStatement();
                        String SQL = "SELECT * FROM user";
                        ResultSet rs = stmt.executeQuery(SQL);
                        String login = null;
                        if(rs.next()){
                            login = rs.getString("login");
                        }

                        final String login2 = login;

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                EditText user_login = (EditText) findViewById(R.id.text);
                                user_login.setText(login2, TextView.BufferType.EDITABLE);

                            }
                        });


//                ResultSet resultset = statement.executeQuery(/* MY SQL reqquest */);

//                while(resultset.next()){
//                    System.out.println(resultset.getString(/* THE COLUMN AND ROW I WANTED IN MY REQUEST */));
//                }

                    }catch(SQLException e){
                        System.out.println("SQL connection error: " + e.getMessage());
                    }finally {
                        if(conn != null){
                            try{
                                conn.close();
                            }catch (SQLException e){
                                System.out.println("Error while closing the connection: " + e.getMessage());
                                //s
                            }
                        }
                    }

//            Statement stmt = conn.createStatement( );
//            String SQL = "SELECT * FROM user";
//            ResultSet rs = stmt.executeQuery( SQL );
//
//            String login = rs.getString("login");

                }
                catch (Exception e){
                    System.out.println(e.getMessage());
                    System.out.print("Tu jest blad");
                }
            }
        }.start();
    }
}
