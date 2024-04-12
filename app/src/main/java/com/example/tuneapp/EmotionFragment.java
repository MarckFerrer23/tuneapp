package com.example.tuneapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import org.json.JSONObject;

public class EmotionFragment extends Fragment {

    private WebSocket webSocket;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_emotion, container, false);

        // Setup WebSocket connection
        startWebSocket();

        // Initialize emotion buttons
        initializeEmotionButtons(view);

        return view;
    }

    private void startWebSocket() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("ws://your.server.com").build(); // Replace with your WebSocket server URL
        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(@NonNull WebSocket webSocket, @NonNull okhttp3.Response response) {
                // Connection opened
            }

            // Implement other necessary callback methods...
        });
    }

    private void initializeEmotionButtons(View view) {
        // Example for HAPPY button
        Button buttonHappy = view.findViewById(R.id.buttonHappy);
        buttonHappy.setOnClickListener(v -> sendEmotion("HAPPY"));

        // Repeat for other emotions: SAD, ANGRY, FEAR...
        Button buttonSad = view.findViewById(R.id.buttonSad);
        buttonSad.setOnClickListener(v -> sendEmotion("SAD"));

        Button buttonAngry = view.findViewById(R.id.buttonAngry);

        buttonAngry.setOnClickListener(v -> sendEmotion("ANGRY"));

        Button buttonFear = view.findViewById(R.id.buttonFear); // Assuming this is the FEAR button
        buttonFear.setOnClickListener(v -> sendEmotion("FEAR"));
    }

    private void sendEmotion(String emotion) {
        try {
            JSONObject message = new JSONObject();
            message.put("emotion", emotion);
            if (webSocket != null) {
                webSocket.send(message.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (webSocket != null) {
            webSocket.close(1000, "Fragment Destroyed");
        }
    }
}
