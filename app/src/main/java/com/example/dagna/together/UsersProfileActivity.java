package com.example.dagna.together;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.dagna.together.onlineDatabase.AddUser;
import com.example.dagna.together.onlineDatabase.UpdateUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UsersProfileActivity extends AppCompatActivity {
    String json_string, user_id;
    JSONObject jsonObject;
    JSONArray jsonArray;

    Context context;

    TextView Login, City, Note;

    int note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_users_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //user_id=getIntent().getExtras().getString("user_id");
        context = getApplicationContext();

       // Login = (TextView)findViewById(R.id.users_profile_login);
        City = (TextView)findViewById(R.id.users_profile_city);
        Note = (TextView)findViewById(R.id.users_profile_note);



        json_string=getIntent().getExtras().getString("json_data");

        Log.d("USERS ID JSON", json_string);
        try {
            // Log.d("login", login);
            jsonObject = new JSONObject(json_string);
            jsonArray = jsonObject.getJSONArray("server_response");
            JSONObject JO = jsonArray.getJSONObject(0);

            String db_login, db_city, db_note, db_id;

            db_login = JO.getString("login");
            db_city = JO.getString("city");
            db_note=JO.getString("grade");

            user_id=JO.getString("user_id");

            getSupportActionBar().setTitle(db_login + "'s profile");

            City.append(db_city);
            Note.setText(db_note);

            note = Integer.parseInt(Note.getText().toString());


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void plus(View view){
        note++;
        Log.d("note", Integer.toString(note));
        Note.setText(Integer.toString(note));
        updateUser(user_id, Integer.toString(note));
    }

    public void minus(View view){
        note--;
        Note.setText(Integer.toString(note));
        updateUser(user_id,Integer.toString(note));
    }

    private void updateUser(String id, String grade){
        UpdateUser updateUser = (UpdateUser) new UpdateUser(new UpdateUser.AsyncResponse() {

            @Override
            public void processFinish(String output) {

                Log.d("OUTPUT", output);
                if (output.equals("error")) {
                    Log.d("ERROR", "ERROR");

                } else {
                   Log.d("ok","ok");
                }
            }
        }).execute(id, grade);
    }

}
