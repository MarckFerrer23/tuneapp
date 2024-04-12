package com.example.tuneapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment {

    private WebSocket webSocket;
    private TextView emotionText; // TextView for displaying "User is Happy"
    private ImageView emotionImage;
    private final Map<String, Integer> emotionMap = new HashMap<>();
    private static final String TAG = "HomeFragment";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize views
        emotionText = view.findViewById(R.id.textView4);
        emotionImage = view.findViewById(R.id.imageView9);

        // Initialize emotion map with drawable resource IDs
        initializeEmotionMap();

        // Start WebSocket connection
        startWebSocket();
        return view;
    }

    private void initializeEmotionMap() {
        emotionMap.put("HAPPY", R.drawable.share_happy_emoji);
        emotionMap.put("SAD", R.drawable.share_sad_emoji);
        emotionMap.put("ANGRY", R.drawable.share_angry_emoji);
        emotionMap.put("FEAR", R.drawable.share_fear_emoji);
    }

    private void startWebSocket() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("wss://192.168.8.100:8080/").build();
        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(@NonNull WebSocket webSocket, @NonNull okhttp3.Response response) {
                getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Connected to server", Toast.LENGTH_SHORT).show());
                Log.d(TAG, "WebSocket opened");
            }

            @Override
            public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
                Log.d(TAG, "Received: " + text);
                Integer drawableResource = emotionMap.getOrDefault(text.toUpperCase(), 0);
                String userName = getUserNameFromPreferences();
                getActivity().runOnUiThread(() -> {
                    if (drawableResource != 0) {
                        emotionImage.setImageResource(drawableResource);
                        emotionText.setText(userName + " is " + text);
                    } else {
                        Log.d(TAG, "Invalid emotion key received: " + text);
                    }
                });
            }

            @Override
            public void onClosing(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
                getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Disconnected: " + reason, Toast.LENGTH_SHORT).show());
                Log.d(TAG, "Closing: " + reason);
                webSocket.close(1000, null);
            }

            @Override
            public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, okhttp3.Response response) {
                getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "WebSocket connection failed: " + t.getMessage(), Toast.LENGTH_LONG).show());
                Log.e(TAG, "Error on WebSocket", t);
                if (response != null) {
                    Log.e(TAG, "Failed response: " + response.toString());
                }
            }
        });
    }

    private String getUserNameFromPreferences() {
        SharedPreferences prefs = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        return prefs.getString("UserName", "User"); // Default to "User" if not found
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (webSocket != null) {
            webSocket.close(1000, "Fragment Destroyed");
            Log.d(TAG, "WebSocket closed");
        }
    }
}
