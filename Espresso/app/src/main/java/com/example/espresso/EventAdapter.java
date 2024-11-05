package com.example.espresso;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class EventAdapter extends BaseAdapter {
    Context context;
    String[] name;
    String[] date;
    String[] time;
    String[] location;
    String[] image;
    LayoutInflater inflater;

    public EventAdapter(Context c, String[] name, String[] date, String[] time, String[] location, String[] image){
        this.context = c;
        this.name = name;
        this.date = date;
        this.time = time;
        this.location = location;
        this.image = image;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return name.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.event_item, null);
        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView date = (TextView) convertView.findViewById(R.id.date);
        TextView location = (TextView) convertView.findViewById(R.id.location);
        ImageView poster = (ImageView) convertView.findViewById(R.id.poster);
        name.setText(this.name[position]);
        date.setText(String.format("%s %s", this.date[position], this.time[position]));
        location.setText(this.location[position]);
        Picasso.get().load(this.image[position]).into(poster);
        return convertView;
    }
}
