package com.example.dagna.together;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.enableDefaults();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String login = preferences.getString("login", "");


        if(login.equals("")){
            Intent intent = new Intent(this, RegisterOrLoginActivity.class);
            startActivity(intent);
            finish();
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

        return super.onOptionsItemSelected(item);
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
