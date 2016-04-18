package com.example.dagna.together.helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.dagna.together.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dagna on 18/04/2016.
 */
public class EventAdapter extends ArrayAdapter {
    List list = new ArrayList();
    public EventAdapter(Context context, int resource) {
        super(context, resource);
    }


    public void add(Events object) {
        super.add(object);
        list.add(object);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row;
        row=convertView;
        EventHolder eventHolder;
        if(row==null){
            LayoutInflater layoutInflater = (LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row=layoutInflater.inflate(R.layout.row_layout,parent,false);
            eventHolder= new EventHolder();
            eventHolder.event_name= (TextView) row.findViewById(R.id.event_name);
            eventHolder.event_description= (TextView) row.findViewById(R.id.event_description);
            row.setTag(eventHolder);

        }
        else{
            eventHolder=(EventHolder) row.getTag();
        }

        Events events = (Events)this.getItem(position);
        eventHolder.event_name.setText(events.getName());
        eventHolder.event_description.setText(events.getDescription());

        return row;
    }


    static class EventHolder{
        TextView event_name, event_description;
    }
}
