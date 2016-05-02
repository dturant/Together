package com.example.dagna.together.helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.dagna.together.R;
import com.example.dagna.together.domain.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dagna on 18/04/2016.
 */
public class UserAdapter extends ArrayAdapter<Users> {
    List list = new ArrayList();
    public UserAdapter(Context context, int resource) {
        super(context, resource);
    }


    public void add(Users object) {
        super.add(object);
        list.add(object);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Users getItem(int position) {
        return (Users)list.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row;
        row=convertView;
        UserHolder userHolder;
        if(row==null){
            LayoutInflater layoutInflater = (LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row=layoutInflater.inflate(R.layout.content_user_list,parent,false);
            userHolder= new UserHolder();
            userHolder.user_login= (TextView) row.findViewById(R.id.login);

            row.setTag(userHolder);

        }
        else{
            userHolder=(UserHolder) row.getTag();
        }

        Users users = (Users)this.getItem(position);
        userHolder.user_login.setText(users.getLogin());

        return row;
    }


    static class UserHolder{
        TextView user_login;
    }
}
