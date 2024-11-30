package com.example.espresso.Admin;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.espresso.R;
import com.example.espresso.Attendee.User;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UsersListAdapter extends BaseAdapter {
    private final Context context;
    private final List<User> users;
    private final LayoutInflater inflater;
    private final FirebaseFirestore db;

    /**
     * Constructor for the UsersListAdapter.
     *
     * @param context The context where the adapter is used, typically an Activity or Fragment.
     * @param users   The list of users to be displayed in the ListView.
     */
    public UsersListAdapter(Context context, List<User> users) {
        this.context = context;
        this.users = users;
        this.inflater = LayoutInflater.from(context);
        this.db = FirebaseFirestore.getInstance();
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int position) {
        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Returns a view for the user item at the specified position.
     *
     * @param position    The position of the item within the list.
     * @param convertView The old view to reuse, if possible.
     * @param parent      The parent view to which the new view will be attached.
     * @return The view for the user item at the given position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_user, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.name = convertView.findViewById(R.id.user_name);
            viewHolder.profileImage = convertView.findViewById(R.id.user_profile_picture);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Get the current user
        User user = users.get(position);

        // Load user name from Firestore (since it is not part of the User class now)
        loadUserName(user, viewHolder);

        // Load profile picture from Firebase Storage
        String path = "pfps/" + user.getDeviceID() + ".png";
        StorageReference profileRef = FirebaseStorage.getInstance().getReference().child(path);
        viewHolder.profileImage.setTag(user.getDeviceID());

        profileRef.getDownloadUrl().addOnSuccessListener(uri -> {
            if (user.getDeviceID().equals(viewHolder.profileImage.getTag())) {
                Picasso.get().load(uri).into(viewHolder.profileImage);
            }
        }).addOnFailureListener(exception -> {
            Log.e("UsersListAdapter", "Error loading profile image for user: " + user.getDeviceID(), exception);
        });

        return convertView;
    }

    /**
     * Load the user name from Firestore.
     *
     * @param user The user object.
     * @param viewHolder The view holder for the item.
     */
    private void loadUserName(User user, ViewHolder viewHolder) {
        db.collection("users").whereEqualTo("deviceID", user.getDeviceID())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        QueryDocumentSnapshot document = (QueryDocumentSnapshot) task.getResult().getDocuments().get(0);
                        String userName = document.getString("name");
                        if (userName != null) {
                            viewHolder.name.setText(userName);
                        }
                    } else {
                        Log.e("UsersListAdapter", "Error getting user name for deviceID: " + user.getDeviceID());
                    }
                });
    }

    /**
     * ViewHolder pattern to hold references to the views for each user item.
     */
    static class ViewHolder {
        TextView name;
        ImageView profileImage;
    }
}
