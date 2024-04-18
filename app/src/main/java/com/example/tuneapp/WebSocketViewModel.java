package com.example.tuneapp;

import androidx.lifecycle.ViewModel;

public class WebSocketViewModel extends ViewModel {
    private WebSocketManager webSocketManager;

    public WebSocketViewModel() {
        webSocketManager = new WebSocketManager(App.getAppContext());
    }

    public void sendEmotion(String emotion) {
        webSocketManager.sendMessage(emotion);
    }

    @Override
    protected void onCleared() {
        webSocketManager.closeConnection();
        super.onCleared();
    }

    public void onResume() {
        webSocketManager.onResume();
    }

    public void onPause() {
        webSocketManager.onPause();
    }
}
