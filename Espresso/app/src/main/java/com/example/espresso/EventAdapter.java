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
/**
 * An adapter to display a list of events in a ListView. Each event is displayed using a custom layout
 * (`event_item`) that includes the event name, date, location, and poster image.
 *
 * The adapter also fetches data from Firestore to check if the current user is already a participant
 * of the event, and if so, hides the event from the list.
 */
public class EventAdapter extends BaseAdapter {

    Context context;  // Context to access resources
    List<Event> events;  // List of events to display
    LayoutInflater inflater;  // Inflater to create views from the layout resource

    /**
     * Constructor to initialize the adapter with the given context and list of events.
     *
     * @param c The context in which the adapter is used.
     * @param events The list of events to display.
     */
    public EventAdapter(Context c, List<Event> events){
        this.context = c;
        this.events = events;
        inflater = LayoutInflater.from(context);  // Initialize the inflater to create views
    }

    /**
     * Returns the number of items in the list (number of events).
     *
     * @return The number of events.
     */
    @Override
    public int getCount() {
        return events.size();
    }

    /**
     * Returns the event at the specified position in the list.
     *
     * @param position The position of the item within the data set.
     * @return The event at the specified position.
     */
    @Override
    public Object getItem(int position) {
        return events.get(position);
    }

    /**
     * Returns the ID of the item at the specified position (in this case, it's just the position index).
     *
     * @param position The position of the item within the data set.
     * @return The ID of the item (position index).
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Returns a view that displays the data for a specific event at the given position.
     * Uses the ViewHolder pattern to optimize performance by reusing views.
     *
     * @param position The position of the item within the data set.
     * @param convertView A recycled view to reuse, if available.
     * @param parent The parent view group.
     * @return The view displaying the event data.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // ViewHolder pattern to optimize view inflation
        ViewHolder viewHolder;

        // If convertView is null, inflate a new view and set up the ViewHolder
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.event_item, parent, false);

            // Initialize the ViewHolder and bind it to the view
            viewHolder = new ViewHolder();
            viewHolder.name = convertView.findViewById(R.id.name);
            viewHolder.date = convertView.findViewById(R.id.date);
            viewHolder.location = convertView.findViewById(R.id.location);
            viewHolder.image = convertView.findViewById(R.id.poster);

            // Set the ViewHolder as a tag to reuse in the future
            convertView.setTag(viewHolder);
        } else {
            // If convertView is not null, retrieve the ViewHolder to avoid inflation
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Get the current event
        Event event = events.get(position);
        String deviceID = new User(context).getDeviceID();  // Get the current user's device ID

        // Fetch participant data from Firestore to determine if the user is already registered
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("events")
                .document(event.getId())
                .collection("participants")
                .document(deviceID);  // Check the current user in the participants subcollection

        View finalConvertView = convertView;
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // If the user is already a participant, hide the event view
                    finalConvertView.setVisibility(View.GONE);
                } else {
                    // Populate the view with event data if the user is not a participant
                    viewHolder.name.setText(event.getName());
                    viewHolder.date.setText(String.format("%s %s", event.getDate(), event.getTime()));
                    viewHolder.location.setText(event.getFacility());

                    // Fetch the event's image URL and load it into the ImageView using Picasso
                    event.getUrl(url -> {
                        if (url != null) {
                            Log.d("ImageURL", "Fetched URL: " + url);
                            // Use an image loading library like Picasso to load the image
                            Picasso.get().load(url).into(viewHolder.image);
                        } else {
                            Log.d("ImageURL", "Failed to fetch URL.");
                        }
                    });
                }
            } else {
                Log.d("Firestore", "Error fetching event participant data.", task.getException());
            }
        });

        return convertView;
    }

    /**
     * A ViewHolder class to hold references to the views inside each list item for better performance.
     */
    static class ViewHolder {
        TextView name;  // TextView for the event name
        TextView date;  // TextView for the event date and time
        TextView location;  // TextView for the event location
        ImageView image;  // ImageView for the event poster image
    }
}
