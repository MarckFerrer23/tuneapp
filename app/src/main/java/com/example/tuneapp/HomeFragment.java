package com.example.tuneapp;

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

import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment {

    private WebSocket webSocket;
    private TextView emotionText;
    private ImageView emotionImage;
    private final Map<String, Integer> emotionMap = new HashMap<>();
    private static final String TAG = "HomeFragment";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        emotionText = view.findViewById(R.id.textView4);
        emotionImage = view.findViewById(R.id.imageView9);
        initializeEmotionMap();
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
        OkHttpClient client = new OkHttpClient.Builder()
                .sslSocketFactory(getSSLSocketFactory(), getTrustManager())
                .hostnameVerifier((hostname, session) -> true)  // Trust all hostnames for this example, not recommended for production
                .build();

        Request request = new Request.Builder().url("wss://192.168.55.106:8080").build();
        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(@NonNull WebSocket webSocket, @NonNull okhttp3.Response response) {
                getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Connected to server", Toast.LENGTH_SHORT).show());
                Log.d(TAG, "WebSocket opened");
            }

            @Override
            public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
                Log.d(TAG, "Received: " + text);
                handleReceivedText(text);
            }

            @Override
            public void onClosing(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
                getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Disconnected: " + reason, Toast.LENGTH_SHORT).show());
                webSocket.close(1000, null);
                Log.d(TAG, "Closing: " + reason);
            }

            @Override
            public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, okhttp3.Response response) {
                getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "WebSocket connection failed: " + t.getMessage(), Toast.LENGTH_LONG).show());
                Log.e(TAG, "Error on WebSocket", t);
            }
        });
    }

    private void handleReceivedText(String text) {
        String formattedText = text.trim().toUpperCase();
        Integer drawableResource = emotionMap.get(formattedText);
        getActivity().runOnUiThread(() -> {
            if (drawableResource != null) {
                emotionImage.setImageResource(drawableResource);
                emotionText.setText("User is " + formattedText);
            } else {
                Log.d(TAG, "Unhandled emotion key: " + formattedText);
                emotionImage.setImageResource(R.drawable.share_fear_emoji);  // Ensure you have a default image resource
                emotionText.setText("No emotion detected");
            }
        });
    }

    private SSLSocketFactory getSSLSocketFactory() {
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream cert = getResources().openRawResource(R.raw.certificate);  // Ensure your certificate is in res/raw
            Certificate ca = cf.generateCertificate(cert);
            cert.close();

            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(keyStore);

            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, tmf.getTrustManagers(), null);
            return context.getSocketFactory();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create SSLSocketFactory", e);
        }
    }

    private X509TrustManager getTrustManager() {
        try {
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init((KeyStore) null);
            TrustManager[] trustManagers = tmf.getTrustManagers();
            if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
                throw new IllegalStateException("Unexpected default trust managers:" + java.util.Arrays.toString(trustManagers));
            }
            return (X509TrustManager) trustManagers[0];
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Trust Manager", e);
        }
    }
}
