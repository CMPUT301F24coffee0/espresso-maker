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
import com.google.firebase.storage.FirebaseStorage;
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
        convertView = inflater.inflate(R.layout.event_item, null);

        Event event = events.get(position);
        String deviceID = new User(context).getDeviceID();
        // Check if user already joined the lottery
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("events").document(event.getId()).collection("participants").document(deviceID);
        View finalConvertView = convertView;
        View finalConvertView1 = convertView;
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    finalConvertView.setVisibility(View.GONE);
                }
                else {
                    TextView name = (TextView) finalConvertView1.findViewById(R.id.name);
                    TextView date = (TextView) finalConvertView1.findViewById(R.id.date);
                    TextView location = (TextView) finalConvertView1.findViewById(R.id.location);
                    ImageView image = (ImageView) finalConvertView1.findViewById(R.id.poster);

                    name.setText(event.getName());
                    date.setText(String.format("%s %s", event.getDate(), event.getTime()));
                    location.setText(event.getFacility());

                    // Fetch the image URL from Firebase Storage and load it into the ImageView
                    event.getUrl(new Event.OnUrlFetchedListener() {
                        @Override
                        public void onUrlFetched(String url) {
                            if (url != null) {
                                Log.d("ImageURL", "Fetched URL: " + url);
                                // Use the URL as needed, e.g., load it into an ImageView with Glide or Picasso
                                Picasso.get().load(url).into(image);
                            } else {
                                Log.d("ImageURL", "Failed to fetch URL.");
                            }
                        }
                    });
                }
            }
        });

        return convertView;
    }
}
