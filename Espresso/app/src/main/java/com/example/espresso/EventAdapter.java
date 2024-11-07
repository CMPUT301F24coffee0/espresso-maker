package com.example.espresso;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;
import java.util.List;

public class EventAdapter extends BaseAdapter {
    Context context;
    List<Event> events;
    LayoutInflater inflater;

    public EventAdapter(Context c, List<Event> events){
        this.context = c;
        this.events = events;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return events.size();
    }

    @Override
    public Object getItem(int position) {
        return events.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // ViewHolder pattern to improve performance
        ViewHolder viewHolder;

        // If convertView is null, inflate a new view and set up the ViewHolder
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.event_item, parent, false);

            // Initialize ViewHolder and associate it with the view
            viewHolder = new ViewHolder();
            viewHolder.name = convertView.findViewById(R.id.name);
            viewHolder.date = convertView.findViewById(R.id.date);
            viewHolder.location = convertView.findViewById(R.id.location);
            viewHolder.image = convertView.findViewById(R.id.poster);

            // Set the ViewHolder as a tag on the convertView so it can be reused
            convertView.setTag(viewHolder);
        } else {
            // If convertView is not null, retrieve the ViewHolder
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Get the current event data
        Event event = events.get(position);
        String deviceID = new User(context).getDeviceID();

        // Fetch the event participant data from Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("events")
                .document(event.getId());

        View finalConvertView = convertView;
        docRef.get().addOnCompleteListener(task -> {
            Log.d("Firestore", "Fetching event participant data...");
            if (task.isSuccessful()) {
                Log.d("Firestore", "Fetched event participant data." + task.getResult());
                DocumentSnapshot document = task.getResult();

                    viewHolder.name.setText(event.getName());
                    viewHolder.date.setText(String.format("%s %s", event.getDate(), event.getTime()));
                    viewHolder.location.setText(event.getFacility());

                    // Fetch and load the image URL
                    event.getUrl(url -> {
                        if (url != null) {
                            Log.d("ImageURL", "Fetched URL: " + url);
                            // Use an image loading library like Picasso to load the image
                            Picasso.get().load(url).into(viewHolder.image);
                        } else {
                            Log.d("ImageURL", "Failed to fetch URL.");
                        }
                    });
            } else {
                Log.d("Firestore", "Error fetching event participant data.", task.getException());
            }
        });

        return convertView;
    }

    // ViewHolder class to hold references to the views
    static class ViewHolder {
        TextView name;
        TextView date;
        TextView location;
        ImageView image;
    }

}