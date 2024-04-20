package com.example.tuneapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;
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
import java.util.Collections;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class HomeFragment extends Fragment {

    private WebSocket webSocket;
    private TextView emotionText, suggestionsText;
    private ImageView emotionImage;
    private final Map<String, Integer> emotionMap = new HashMap<>();
    private final Map<String, List<String>> emotionToActivities = new HashMap<>();
    private static final String TAG = "HomeFragment";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        emotionText = view.findViewById(R.id.textViewEmotion);
        suggestionsText = view.findViewById(R.id.textViewSuggestions);
        emotionImage = view.findViewById(R.id.imageView9);

        initializeEmotionMap();
        initializeActivityMap();
        initializeEmotionButtons(view);
        startWebSocket();

        return view;
    }

    private void initializeEmotionMap() {
        emotionMap.put("HAPPY", R.drawable.share_happy_emoji);
        emotionMap.put("SAD", R.drawable.share_sad_emoji);
        emotionMap.put("ANGRY", R.drawable.share_angry_emoji);
        emotionMap.put("FEAR", R.drawable.share_fear_emoji);
    }

    private void initializeActivityMap() {
        emotionToActivities.put("HAPPY", Arrays.asList("Continue current activity", "Play a favorite song", "Dance to upbeat songs", "Watch favorite cartoons",
                "Play interactive video games", "Engage in sports", "Draw and color", "Build with Legos",
                "Read a favorite book", "Participate in a treasure hunt", "Do a fun science experiment", "Visit a playground",
                "Engage in sensory play", "Play a musical instrument", "Cook or bake simple recipes", "Have a picnic",
                "Play pretend games", "Participate in a dance class", "Go for a fun ride", "Playing with pets", "Go bowling",
                "Painting large canvas", "Performing a play", "Doing magic tricks", "Attending a children's concert",
                "Visiting a zoo", "Going on a nature walk", "Collecting shells or rocks", "Making jewelry", "Bird watching",
                "Going to a petting zoo", "Storytelling session", "Participate in photography walk", "Explore new apps",
                "Conduct a mini orchestra", "Create a scrapbook", "Decorate personal space", "Assemble a model",
                "Watching a funny movie", "Visit an amusement park", "Go to a children's museum", "Playing mini-golf"
        ));
        emotionToActivities.put("SAD", Arrays.asList("Hug or physical comfort", "Play soothing music", "Drawing or painting", "Engaging in slow sensory activities",
                "Cuddling with a weighted blanket", "Reading comforting stories", "Talking to a friend or counselor",
                "Writing in a journal", "Making comfort food", "Looking at family photos", "Doing puzzles", "Gardening",
                "Watching calming documentaries", "Floating in a pool", "Having a tea party", "Using a sandbox",
                "Creating with clay", "Building a fort with blankets", "Doing a quiet, structured activity",
                "Visiting a quiet, scenic place", "Engaging in a favorite hobby", "Collecting and organizing items",
                "Doing gentle, guided meditation", "Looking through an art book", "Making a simple craft",
                "Engaging in a slow-paced video game", "Doing a simple woodworking project", "Creating a simple electronic project",
                "Baking bread or pastries", "Assembling a simple puzzle", "Watching a slow-paced, cheerful cartoon",
                "Participating in a community art project", "Making a homemade pizza", "Visiting a botanical garden",
                "Going fishing", "Attending a quiet matinee movie", "Visiting a museum on a quiet day", "Participating in a library story hour",
                "Making a photo album", "Learning to take pictures with a simple camera", "Painting a picture using watercolors",
                "Doing beadwork", "Participating in a music therapy session"
        ));
        emotionToActivities.put("ANGRY", Arrays.asList("Engage in vigorous sports", "Do martial arts or boxing", "Squeeze stress balls", "Tear up scrap paper",
                "Stomp on bubble wrap", "Draw or scribble out anger", "Engage in competitive video games", "Do intense physical exercise",
                "Participate in a drumming session", "Shout into or sing loudly into a pillow", "Construct something complex",
                "Do a high-energy dance", "Clean or organize a space aggressively", "Gardening with intensity", "Chop wood",
                "Bike on a challenging path", "Swim laps", "Write angry letters", "Blow balloons and pop them", "Smash old ceramics",
                "Engage in supervised destruction activities", "Pound clay or dough", "Use a punching bag", "Scream in a private space",
                "Run sprints", "Hike up a steep hill", "Lift weights", "Tear old newspapers", "Do hard yard work", "Engage in a vigorous workout",
                "Participate in a fast-paced sport", "Climb safely", "Engage in a fast dance or aerobics class", "Scrub floors or walls",
                "Wash a car by hand", "Play squash or racquetball", "Throw darts", "Jump on a trampoline", "Participate in a loud sports event",
                "Break sticks", "Kick a soccer ball against a wall", "Draw with charcoals violently", "Cut up cardboard",
                "Smash ice cubes", "Use a hammer to crush ice", "Do heavy gardening", "Rip an old t-shirt", "Flatten cans for recycling",
                "Shred documents", "Do push-ups or sit-ups"
        ));
        emotionToActivities.put("FEAR", Arrays.asList("Watch comforting videos or movies", "Listen to soothing music", "Hold a stuffed animal or comfort object",
                "Use a weighted blanket", "Engage in deep breathing exercises", "Meditate or use guided imagery",
                "Draw what scares them and discuss", "Read reassuring stories", "Be in a small, cozy space", "Practice slow yoga",
                "Have a trusted person nearby", "Engage in favorite calm hobbies", "Play with pets", "Use sensory tools",
                "Practice aromatherapy with calming scents", "Drink warm tea or hot chocolate", "Watch slow and calm nature documentaries",
                "Be wrapped in a hug or massaged gently", "Look at peaceful nature scenes", "Listen to nature sounds",
                "Take slow, gentle walks in nature", "Engage in crafts requiring attention", "Sit by a fireplace or campfire",
                "Stargaze", "Float in a pool safely", "Sit in a rocking chair or hammock", "Read books about overcoming fear",
                "Bake cookies or a simple cake", "Garden focusing on soft-textured plants", "Have a picnic in a protected area",
                "Create art with calming colors", "Play therapy sessions", "Talk to a counselor or therapist", "Watch uplifting cartoons",
                "Group support activities", "Do simple puzzles", "Organize a room or space", "Visit a calm animal farm or zoo",
                "Engage in light photography", "Go on a calm boat ride", "Learn about stars and planets", "Make a simple birdhouse",
                "Do a guided museum tour", "Listen to soft, classical music", "Visit a butterfly garden", "Go to a quiet beach",
                "Collect and paint rocks", "Make simple jewelry", "Practice Tai Chi", "Watch a slow-paced play or musical"
        ));
    }

    private void initializeEmotionButtons(View view) {
        Button buttonHappy = view.findViewById(R.id.buttonHappy);
        buttonHappy.setOnClickListener(v -> sendEmotion("HAPPY"));

        Button buttonSad = view.findViewById(R.id.buttonSad);
        buttonSad.setOnClickListener(v -> sendEmotion("SAD"));

        Button buttonAngry = view.findViewById(R.id.buttonAngry);
        buttonAngry.setOnClickListener(v -> sendEmotion("ANGRY"));

        Button buttonFear = view.findViewById(R.id.buttonFear);
        buttonFear.setOnClickListener(v -> sendEmotion("FEAR"));
    }

    private void sendEmotion(String emotion) {
        if (webSocket != null) {
            webSocket.send(emotion);
        } else {
            Toast.makeText(getActivity(), "WebSocket is not connected.", Toast.LENGTH_SHORT).show();
        }
    }

    private void startWebSocket() {
        OkHttpClient client = new OkHttpClient.Builder()
                .sslSocketFactory(getSSLSocketFactory(), getTrustManager())
                .hostnameVerifier((hostname, session) -> true)
                .build();

        Request request = new Request.Builder().url("wss://192.168.55.108:8080").build();
        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(@NonNull WebSocket webSocket, @NonNull okhttp3.Response response) {
                getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Connected to server", Toast.LENGTH_SHORT).show());
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
            }

            @Override
            public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, okhttp3.Response response) {
                getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "WebSocket connection failed: " + t.getMessage(), Toast.LENGTH_LONG).show());
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
                List<String> activities = selectRandomActivities(formattedText);
                suggestionsText.setText("Activities: " + String.join(", ", activities));
            } else {
                Log.d(TAG, "Unhandled emotion key: " + formattedText);
                emotionImage.setImageResource(R.drawable.share_fear_emoji);
                emotionText.setText("No emotion detected");
                suggestionsText.setText("Activities: No suggestions available");
            }
        });
    }

    private List<String> selectRandomActivities(String emotion) {
        List<String> availableActivities = emotionToActivities.getOrDefault(emotion, new ArrayList<>());
        Collections.shuffle(availableActivities);
        return availableActivities.subList(0, Math.min(3, availableActivities.size()));
    }

    private SSLSocketFactory getSSLSocketFactory() {
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream cert = getResources().openRawResource(R.raw.certificate);
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
                throw new IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers));
            }
            return (X509TrustManager) trustManagers[0];
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Trust Manager", e);
        }
    }
}