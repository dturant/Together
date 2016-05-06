package com.example.dagna.together;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.dagna.together.helpers.GeneralHelpers;
import com.example.dagna.together.helpers.UserAdapter;
import com.example.dagna.together.helpers.Users;
import com.example.dagna.together.onlineDatabase.GetUserById;

/**
 * Created by Dagna on 05/05/2016.
 */
public class ParticipantsTabFragment extends Fragment {
    public static String event_id;
    public static Context context;
    public static String json_string;
    public static ConnectivityManager connMan;

    ListView listView;
    UserAdapter userAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.participants, container, false);

        displayUsers(view);

        return view;
    }

    private void displayUsers(View view) {
        listView = (ListView) view.findViewById(R.id.eventListView);
        userAdapter = new UserAdapter(context, R.layout.content_user_list);
        listView.setAdapter(userAdapter);
        //userAdapter=new UserAdapter(context, R.layout.content_user_list);

        Log.e("SPIERDOLOEN", "GOWNO");
        for (int i = 0; i < EventActivity.usersList.size(); i++) {
            userAdapter.add(EventActivity.usersList.get(i));
        }

        //TODO dodac te glupia liste o listview czy cus
        listView.setClickable(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parentView, View childView,
                                    int position, long id) {
                if (GeneralHelpers.isNetworkAvailable(connMan)) {
                    String userId = EventActivity.usersList.get(position).getId();
                    Log.d("userid", userId);
                    displayUser(userId);

                } else {
                    GeneralHelpers.createNetErrorDialog(context);
                }
            }

            public void onNothingSelected(AdapterView parentView) {

            }
        });
    }

    private void displayUser(final String userId)
    {
        Log.d("USERS ID", userId);
        GetUserById getUserById = (GetUserById) new GetUserById(new GetUserById.AsyncResponse() {

            @Override
            public void processFinish(String output) {
                Log.d("USERS ID OUTPUT", output);
                if (GetUserById.json_string.length() < 30) {
                    Log.d("fail!!!!", "fail :(");

                } else {
                    //Log.d("data!!!!!!!", GetUserByLogin.json_string);
                    json_string = GetUserById.json_string;
                    Intent intent = new Intent(context, UsersProfileActivity.class);
                    intent.putExtra("json_data", json_string);
                    intent.putExtra("user_id", userId);
                    Log.d("event_id from timeline", userId);
                    startActivity(intent);

                }
            }
        }).execute(userId);


    }
}
