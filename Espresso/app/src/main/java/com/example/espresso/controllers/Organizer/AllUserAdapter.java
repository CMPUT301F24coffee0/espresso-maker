package com.example.espresso.controllers.Organizer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.espresso.models.Organizer.AllUserModel;
import com.example.espresso.R;

import java.util.List;

/**
 * Custom ArrayAdapter to display a list of users and their event status.
 * Each item in the list represents a user and their status related to a specific event.
 */
public class AllUserAdapter extends ArrayAdapter<AllUserModel> {
    private final List<AllUserModel> users;
    private final LayoutInflater inflater;

    /**
     * Constructs an AllUserAdapter with the given context and list of users.
     *
     * @param context The context in which the adapter is being used.
     * @param users The list of AllUserModel objects to be displayed.
     */
    public AllUserAdapter(@NonNull Context context, @NonNull List<AllUserModel> users) {
        super(context, R.layout.listitem_all_entrant, users);
        this.users = users;
        this.inflater = LayoutInflater.from(context);
    }

    /**
     * Provides the view for each list item in the list of users, displaying their name and status.
     *
     * @param position The position of the item within the data set.
     * @param convertView A recycled view that can be reused (if possible).
     * @param parent The parent view that this view will be attached to.
     * @return A View representing a single user and their status.
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listitem_all_entrant, parent, false);
            holder = new ViewHolder();
            holder.nameTextView = convertView.findViewById(R.id.mainContent);
            holder.statusTextView = convertView.findViewById(R.id.status);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Populate data
        AllUserModel user = users.get(position);
        holder.nameTextView.setText(user.getName());
        holder.statusTextView.setText(user.getStatus());

        return convertView;
    }

    /**
     * ViewHolder pattern to improve list view performance by caching references to views.
     */
    static class ViewHolder {
        TextView nameTextView;
        TextView statusTextView;
    }
}
