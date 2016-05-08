package com.example.dagna.together;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dagna.together.helpers.GeneralHelpers;
import com.example.dagna.together.helpers.Users;
import com.example.dagna.together.onlineDatabase.JoinEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Dagna on 05/05/2016.
 */
public class InformationTabFragment extends Fragment {
    TextView Name, Category, Description, Country, City, Street,User ;
    static Button Join, UserSigned;
    ListView listView;
    JSONObject jsonObject;
    JSONArray jsonArray;
    public static String event_id;
    public static Context context;
    public static String json_string;
    public static ConnectivityManager connMan;
    String user_id, user_login;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.information, container, false);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        user_id = preferences.getString("id", "");
        user_login = preferences.getString("login", "");

        Name = (TextView)view.findViewById(R.id.event_nameTextView);
        Category = (TextView)view.findViewById(R.id.event_categoryTextView);
        Description = (TextView)view.findViewById(R.id.event_descriptionTextView);
        City = (TextView)view.findViewById(R.id.event_cityTextView);
        User = (TextView)view.findViewById(R.id.event_userTextView);
        Country = (TextView)view.findViewById(R.id.event_countryTextView);
        Street = (TextView)view.findViewById(R.id.event_streetTextView);
        Join = (Button) view.findViewById(R.id.event_addUserButton);
        UserSigned=(Button) view.findViewById(R.id.event_userSigned);

        getInformation();

        return view;
    }

    private void getInformation()
    {
        try {
            // Log.d("login", login);
            jsonObject = new JSONObject(json_string);
            jsonArray = jsonObject.getJSONArray("server_response");
            JSONObject JO = jsonArray.getJSONObject(0);

            String db_id, db_name, db_description, db_category, db_user, db_street_name, db_street_number, db_city, db_zipcode, db_country;

            db_id = JO.getString("event_id");
            db_name = JO.getString("name");
            db_description = JO.getString("description");
            db_category=JO.getString("category");
            db_user = JO.getString("user");
            db_street_name=JO.getString("street_name");
            db_street_number=JO.getString("street_number");
            db_city=JO.getString("city");
            db_zipcode=JO.getString("zipcode");
            db_country=JO.getString("country");

            //getSupportActionBar().setTitle(db_name);
            Name.append(" " + db_name);
            Category.append(" " + db_category);
            Description.append(" " + db_description);
            City.append(" " + db_city);
            Country.append(" " + db_country);
            Street.append(" " + db_street_name);
            User.append(" " + db_user);

            Log.d("user_login", user_login);
            Log.d("user", db_user);
            if(user_login.equals(db_user)){
                Join.setVisibility(View.INVISIBLE);
            }

            //Join.setVisibility(View.VISIBLE);

            Log.d("userslist", Integer.toString(EventActivity.usersList.size()));
            for(int i = 0; i < EventActivity.usersList.size(); i++){
                Log.d("ids", EventActivity.usersList.get(i).getId() + " " + user_id);
                if(EventActivity.usersList.get(i).getId().equals(user_id)){
                    Join.setVisibility(View.INVISIBLE);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void changeButton(boolean isSigned){
        if(isSigned){
            Join.setVisibility(View.INVISIBLE);
        }
    }


}
