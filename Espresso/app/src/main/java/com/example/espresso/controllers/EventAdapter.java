package com.example.espresso.controllers;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.espresso.R;
import com.example.espresso.models.Event;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import java.util.List;

/**
 * Custom adapter for displaying event data in a ListView.
 * The adapter fetches event details from Firestore and uses a ViewHolder pattern
 * for efficient view recycling.
 */
public class EventAdapter extends BaseAdapter {
    Context context;
    List<Event> events;
    LayoutInflater inflater;

    /**
     * Constructor for the EventAdapter.
     *
     * @param c The context where the adapter is used, typically an Activity or Fragment.
     * @param events  The list of events to be displayed in the ListView.
     */
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

    /**
     * Returns a view for the event item at the specified position.
     * This method is optimized using the ViewHolder pattern.
     *
     * @param position The position of the item within the list.
     * @param convertView The old view to reuse, if possible.
     * @param parent The parent view to which the new view will be attached.
     * @return The view for the event item at the given position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // ViewHolder pattern to improve performance
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.event_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.name = convertView.findViewById(R.id.name);
            viewHolder.date = convertView.findViewById(R.id.date);
            viewHolder.location = convertView.findViewById(R.id.location);
            viewHolder.image = convertView.findViewById(R.id.poster);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Get the current event data
        Event event = events.get(position);

        // Update views with event data
        viewHolder.name.setText(event.getName());
        viewHolder.date.setText(String.format("%s %s", event.getDate(), event.getTime()));
        viewHolder.location.setText(event.getFacility());

        // Load the image from Storage if needed
        String path = "posters/" + event.getId() + ".png";
        StorageReference posterRef = FirebaseStorage.getInstance().getReference().child(path);
        viewHolder.image.setTag(event.getId());  // Set a tag for tracking

        posterRef.getDownloadUrl().addOnSuccessListener(uri -> {
            if (event.getId().equals(viewHolder.image.getTag())) {
                Picasso.get().load(uri).into(viewHolder.image);
            }
        }).addOnFailureListener(exception -> {
            Log.e("Event", path);
            Log.e("Event", "Error getting download URL for poster", exception);
        });

        return convertView;
    }

    /**
     * ViewHolder pattern to hold references to the views for each event item.
     * This improves performance by preventing repeated calls to findViewById.
     */
    // ViewHolder class to hold references to the views
    static class ViewHolder {
        TextView name;
        TextView date;
        TextView location;
        ImageView image;
    }

}
