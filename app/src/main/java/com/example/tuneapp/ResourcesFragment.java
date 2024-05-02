package com.example.tuneapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
                // Define the URL for TextView1
                String url = "https://www.un.org/en/observances/autism-day";

                // Create an intent to open the URL in a browser
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

                // Start the intent
                startActivity(intent);
            }
        });

        // Set click listener to open URL in browser for TextView2
        seeMoreTextView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Define the URL for TextView2
                String url = "https://www.unesco.org/en/inclusion-education";

                // Create an intent to open the URL in a browser
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

                // Start the intent
                startActivity(intent);
            }
        });

        return view;
    }
}
