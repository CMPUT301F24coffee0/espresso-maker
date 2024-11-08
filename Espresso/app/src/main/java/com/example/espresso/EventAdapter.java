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
import android.content.Context;
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
 * Custom adapter for displaying event data in a ListView.
 * The adapter fetches event details from Firestore and uses a ViewHolder pattern
 * for efficient view recycling.
 */
public class EventAdapter extends BaseAdapter {
    private Context context;
    private List<Event> events;
    private LayoutInflater inflater;

    /**
     * Constructor for the EventAdapter.
     *
     * @param context The context where the adapter is used, typically an Activity or Fragment.
     * @param events  The list of events to be displayed in the ListView.
     */
    public EventAdapter(Context context, List<Event> events) {
        this.context = context;
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

        // Fetch event data from Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("events").document(event.getId());

        // Fetch event participant data from Firestore asynchronously
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    // Populate the ViewHolder views with event data
                    viewHolder.name.setText(event.getName());
                    viewHolder.date.setText(String.format("%s %s", event.getDate(), event.getTime()));
                    viewHolder.location.setText(event.getFacility());

                    // Load event image if available
                    event.getUrl(url -> {
                        if (url != null) {
                            // Use Picasso to load the image URL into the ImageView
                            Picasso.get().load(url).into(viewHolder.image);
                        } else {
                            // Log error if the image URL is unavailable
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
     * ViewHolder pattern to hold references to the views for each event item.
     * This improves performance by preventing repeated calls to findViewById.
     */
    static class ViewHolder {
        TextView name;
        TextView date;
        TextView location;
        ImageView image;
    }
}
