package com.example.tuneapp;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Handler;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class WebSocketManager {
    private OkHttpClient client;
    private WebSocket webSocket;
    private Handler retryHandler = new Handler();
    private int retryCount = 0;
    private static final int MAX_RETRIES = 5;
    private Context context;
    private NetworkChangeReceiver networkChangeReceiver;

    // Constructor that accepts Context
    public WebSocketManager(Context context) {
        this.context = context;
        client = new OkHttpClient.Builder().readTimeout(0, TimeUnit.MILLISECONDS).build();
        networkChangeReceiver = new NetworkChangeReceiver(this);
        startConnection();  // Optionally start connection on initialization
    }

    public void startConnection() {
        Request request = new Request.Builder().url("wss://192.168.0.21:8080").build();
        webSocket = client.newWebSocket(request, new EchoWebSocketListener());
    }

    public void sendMessage(String message) {
        if (webSocket != null) {
            webSocket.send(message);
        } else {
            System.out.println("WebSocket is not connected. Message not sent: " + message);
        }
    }

    private class EchoWebSocketListener extends WebSocketListener {
        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            retryCount = 0; // Reset retry counter on successful connection
            System.out.println("WebSocket opened");
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            System.out.println("Received: " + text);
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            webSocket.close(1000, null);
            System.out.println("Closing: " + reason);
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            System.out.println("WebSocket failure: " + t.getMessage());
            if (shouldRetry()) {
                int delay = calculateBackoff(retryCount);
                retryHandler.postDelayed(WebSocketManager.this::reconnect, delay);
                retryCount++;
            }
        }
    }

    public void reconnect() {
        closeConnection();
        startConnection();
    }

    public void closeConnection() {
        if (webSocket != null) {
            webSocket.close(1000, "Closing Connection");
            webSocket = null;
        }
    }

    private int calculateBackoff(int retryCount) {
        return (int) Math.pow(2, retryCount) * 1000; // Exponential backoff
    }

    private boolean shouldRetry() {
        return retryCount < MAX_RETRIES;
    }

    public void onResume() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(networkChangeReceiver, filter);
    }

    public void onPause() {
        context.unregisterReceiver(networkChangeReceiver);
    }
}
