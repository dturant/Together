package com.example.dagna.together;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
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


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

    public void register(View view){
        try{
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            //String host="jdbc:jtds:sqlserver://http://project-together.cba.pl/project_together_cba_pl";
            //String name="ctb";
            //String password="ifejestfajne";
            String database="project_together_cba_pl";
            String server="mysql.cba.pl";
            String user="ctb";
            String password="ifejestfajne";

            //Connection conn = DriverManager.getConnection(host, name, password);

            String ConnectionURL = "jdbc:jtds:sqlserver://" + server + ";"
                    + "databaseName=" + database + ";user=" + user
                    + ";password=" + password + ";";
            Connection conn = DriverManager.getConnection(ConnectionURL);
            Statement stmt = conn.createStatement( );
            String SQL = "SELECT * FROM user";
            ResultSet rs = stmt.executeQuery( SQL );

            String login = rs.getString("login");

            EditText user_login   = (EditText)findViewById(R.id.text);
            user_login.setText("login", TextView.BufferType.EDITABLE);


        }
        catch (Exception e){
            System.out.println(e.getMessage());
            System.out.print("Tu jest blad");
        }
    }
}
