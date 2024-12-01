package com.example.espresso.EntrantList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.espresso.R;

import java.util.List;

public class AllUserAdapter extends ArrayAdapter<AllUserModel> {
    private final List<AllUserModel> users;
    private final LayoutInflater inflater;

    public AllUserAdapter(@NonNull Context context, @NonNull List<AllUserModel> users) {
        super(context, R.layout.listitem_all_entrant, users);
        this.users = users;
        this.inflater = LayoutInflater.from(context);
    }

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

    static class ViewHolder {
        TextView nameTextView;
        TextView statusTextView;
    }
}
