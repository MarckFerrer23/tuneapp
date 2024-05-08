package com.example.tuneapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;

public class ResourcesFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_resources, container, false);

        // Find the "See more" TextViews
        TextView seeMoreTextView1 = view.findViewById(R.id.textView19);
        TextView seeMoreTextView2 = view.findViewById(R.id.textView17);

        // Set click listener to open URL in browser for TextView1
        seeMoreTextView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUrlInBrowser("https://www.un.org/en/observances/autism-day");
            }
        });

        // Set click listener to open URL in browser for TextView2
        seeMoreTextView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUrlInBrowser("https://www.unesco.org/en/inclusion-education");
            }
        });

        // Find imageView16 and imageView18
        ImageView imageView16 = view.findViewById(R.id.imageView16);
        ImageView imageView18 = view.findViewById(R.id.imageView18);

        // Set click listeners for imageView16
        imageView16.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showZoomedImage(R.drawable.world_autism_awareness_day);
            }
        });

        // Set click listeners for imageView18
        imageView18.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showZoomedImage(R.drawable.girls_pic);
            }
        });

        // Find the learn more button
        Button learnMoreButton = view.findViewById(R.id.learnmorebutton);

        // Set click listener to open URL in browser for Learn More Button
        learnMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUrlInBrowser("https://studycorgi.com/literature-review-on-autism-spectrum-disorder/");
            }
        });

        return view;
    }

    // Method to open URL in browser
    private void openUrlInBrowser(String url) {
        // Create an intent to open the URL in a browser
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

        // Start the intent
        startActivity(intent);
    }

    // Method to show enlarged image in a dialog with a dimmed background
    private void showZoomedImage(int imageResId) {
        // Create a dialog
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_zoom_image);

        // Set a semi-transparent background overlay behind the dialog
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.argb(150, 0, 0, 0)));

        // Find the ImageView in the dialog layout
        ImageView imageView = dialog.findViewById(R.id.imageViewZoomed);
        // Set the image resource to the ImageView
        imageView.setImageResource(imageResId);

        // Set click listener to dismiss the dialog when clicked outside the image
        dialog.findViewById(R.id.layoutDialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        // Show the dialog
        dialog.show();
    }
}
