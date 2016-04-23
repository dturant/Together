package com.example.dagna.together;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.dagna.together.helpers.EventAdapter;
import com.example.dagna.together.helpers.Events;
import com.example.dagna.together.onlineDatabase.GetEventByName;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SearchResultsActivity extends AppCompatActivity {

    String json_string;
    JSONObject jsonObject;
    JSONArray jsonArray;
    EventAdapter eventAdapter;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //wziac cursor z bazki
      //  Cursor c = DatabaseIntentService.getCursor();
      //  updateList(c);


        listView = (ListView)findViewById(R.id.resultsListView);
        eventAdapter=new EventAdapter(this, R.layout.content_search_results);
        listView.setAdapter(eventAdapter);
        json_string=getIntent().getExtras().getString("json_data");

        try {
            jsonObject=new JSONObject(json_string);
            jsonArray=jsonObject.getJSONArray("server_response");

            int count=0;
            String name, description;

            while(count<jsonArray.length()){
                JSONObject JO = jsonArray.getJSONObject(count);
                name=JO.getString("name");
                description=JO.getString("description");
                Events events=new Events(name, description);
                eventAdapter.add(events);
                count++;


            }

            listView.setClickable(true);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView parentView, View childView,
                                        int position, long id) {
                    TextView c = (TextView) childView.findViewById(R.id.title);
                    String eventName= c.getText().toString();
                    Log.d("eventname", eventName);
                    displayEvent(eventName);

                }

                public void onNothingSelected(AdapterView parentView) {

                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

  /*  private void updateList(Cursor cursor)
    {
        String[] fromColumns = {DatabaseHelper.KEY_EVENT_NAME, DatabaseHelper.KEY_DSCRP};
        int[] toViews = {R.id.title, R.id.description};
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.content_event_list, cursor, fromColumns, toViews, 0);

        ListView listView = (ListView) findViewById( R.id.resultsListView );
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
    } */

    private void displayEvent(String eventName)
    {
        GetEventByName getEventByName = (GetEventByName) new GetEventByName(new GetEventByName.AsyncResponse() {

            @Override
            public void processFinish(String output) {

                if (GetEventByName.json_string.length() < 30) {
                    Log.d("fail!!!!", "fail :(");

                } else {
                    //Log.d("data!!!!!!!", GetUserByLogin.json_string);
                    json_string = GetEventByName.json_string;
                    Intent intent = new Intent(getApplicationContext(), EventActivity.class);
                    intent.putExtra("json_data", json_string);
                    startActivity(intent);

                }
            }
        }).execute(eventName);


    }

}
