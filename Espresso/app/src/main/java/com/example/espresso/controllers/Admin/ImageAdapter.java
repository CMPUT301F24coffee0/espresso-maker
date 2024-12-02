package com.example.espresso.controllers.Admin;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.net.URLDecoder;
import com.example.espresso.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Overrides RecyclerView class in order to display images as List elements
 */
public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    private List<String> imageUrls;
    private Context context;

    /**
     * Sets up an Image Adapter object
     * @param imageUrls List of URLs of different images on the app
     */
    public ImageAdapter(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String imageUrl = imageUrls.get(position);

        // Load image
        Picasso.get()
                .load(imageUrl)
                .placeholder(android.R.drawable.progress_indeterminate_horizontal)
                .error(android.R.drawable.ic_dialog_alert)
                .into(holder.imageView);

        // Set up delete button
        holder.deleteButton.setOnClickListener(v -> {
            // Extract the filename from the URL
            String filename = extractFilenameFromUrl(imageUrl);
            // Delete from Firebase Storage
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference().child(filename);
            Log.d("ImageAdapter", "Deleting image from: " + filename);
            storageRef.delete()
                    .addOnSuccessListener(aVoid -> {
                        // Remove from list and update RecyclerView
                        int pos = holder.getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            imageUrls.remove(pos);
                            notifyItemRemoved(pos);

                            // Optional: Show a toast message
                            Toast.makeText(context, "Image deleted successfully", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Handle any errors
                        Toast.makeText(context, "Failed to delete image", Toast.LENGTH_SHORT).show();
                        Log.e("ImageAdapter", "Error deleting image" + filename, e);
                    });
        });
    }

    /**
     * Helper method to extract filename from URL
     * @param url URL for the image
     * @return String filename
     */
    private String extractFilenameFromUrl(String url) {
        try {
            // Split by "/o/" and take the second part
            String[] parts = url.split("/o/");
            if (parts.length > 1) {
                // Decode the path to handle URL-encoded characters
                String decodedPath = URLDecoder.decode(parts[1].split("\\?")[0], "UTF-8");

                Log.d("ImageAdapter", "Extracted path: " + decodedPath);
                return decodedPath;
            }

            Log.e("ImageAdapter", "Could not extract path from URL");
            return "";
        } catch (Exception e) {
            Log.e("ImageAdapter", "Error parsing URL", e);
            return "";
        }
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    /**
     * Adds delete button and ImageView to ViewHolder Object
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageButton deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}

