package com.example.tuneapp;

import static android.R.color.system_background_light;
import static android.R.color.white;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.EditText;
import android.text.InputType;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import android.content.res.ColorStateList;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.security.KeyStore;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import javax.net.ssl.SSLSocketFactory;
import java.io.InputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;








public class HomeFragment extends Fragment {

    private WebSocket webSocket;
    private ImageView emotionImage;
    private TextView emotionText, suggestionsText;
    private LinearLayout activitiesLayout;  // Corrected view reference
    private DatabaseReference databaseReference;
    private String currentEmotion;

    private final Map<String, Integer> emotionMap = new HashMap<>();
    private final Map<String, List<String>> emotionToActivities = new HashMap<>();
    private static final String TAG = "HomeFragment";

    private TextView patientTextView;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fireStore;

    private static final String CHANNEL_ID = "emotion_notification_channel";

    // Notification ID
    private static final int NOTIFICATION_ID = 1;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);


        patientTextView = view.findViewById(R.id.patientName);
        Button addPersonalActivityButton = view.findViewById(R.id.buttonAddPersonalActivity);
        addPersonalActivityButton.setOnClickListener(v -> showAddActivityDialog());
        fAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = fAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DocumentReference docRef = fireStore.collection("users").document(userId);
            docRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String patient = documentSnapshot.getString("patient");

                    // Log the data
                    Log.d("HomeFragment", "Full Name: " + patient);


                    // Set text views
                    patientTextView.setText(patient);
                } else {
                    Log.d("HomeFragment", "Document does not exist");
                }
            }).addOnFailureListener(e -> {
                Log.e("HomeFragment", "Error fetching document", e);
            });
        }

        bindViews(view);
        setupFirebase();
        initializeEmotionMap();
        initializeActivityMap();
        initializeEmotionButtons(view);  // Ensure this method is correctly defined
        startWebSocket();
        return view;
    }

    private void bindViews(View view) {
        emotionText = view.findViewById(R.id.textViewEmotion);
        suggestionsText = view.findViewById(R.id.textViewSuggestions);
        emotionImage = view.findViewById(R.id.imageView9);
        activitiesLayout = view.findViewById(R.id.activitiesLayout);  // Corrected ID reference
    }

    private void setupFirebase() {
        databaseReference = FirebaseDatabase.getInstance().getReference("ActivityRatings");
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

    private void displayActivities(List<String> activities) {
        activitiesLayout.removeAllViews();
        // Assuming you are in an Activity or Fragment context
        int buttonBackgroundColor = getResources().getColor(R.color.yellow);
        int buttonTextColor = getResources().getColor(R.color.black);

        for (String activity : activities) {
            Button activityButton = new Button(getActivity());
            activityButton.setText(activity); // Set the text of the button to the activity
            activityButton.setBackgroundTintList(ColorStateList.valueOf(buttonBackgroundColor)); // Set background color
            activityButton.setTextSize(16); // Set text size
            activityButton.setAllCaps(false); // Disable all caps
            activityButton.setPadding(16, 16, 16, 16); // Set padding
            activityButton.setOnClickListener(v -> showRatingDialog(activity)); // Set click listener
            activitiesLayout.addView(activityButton); // Add the button to the layout
        }
    }


    private void showRatingDialog(String activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Rate this Activity: " + activity);

        final EditText input = new EditText(getActivity());
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("Submit", (dialog, which) -> {
            try {
                int rating = Integer.parseInt(input.getText().toString());
                saveRatingToDatabase(activity, rating);
            } catch (NumberFormatException nfe) {
                Toast.makeText(getActivity(), "Invalid input! Please enter a valid number.", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "NumberFormatException: Invalid input provided", nfe);
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void saveRatingToDatabase(String activity, int rating) {
        String key = (activity + "__" + currentEmotion).replaceAll("[^a-zA-Z0-9 ]", "").toLowerCase().replace(" ", "_");
        DatabaseReference ratingsRef = databaseReference.child(key);
        Log.d(TAG, "Saving rating to: " + ratingsRef.getPath());

        ratingsRef.push().setValue(rating)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Successfully saved rating for " + activity);
                    Toast.makeText(getActivity(), "Rating submitted for " + activity, Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to save rating for " + activity, e);
                    Toast.makeText(getActivity(), "Failed to submit rating for " + activity, Toast.LENGTH_SHORT).show();
                });
    }

    private void startWebSocket() {
        OkHttpClient client = new OkHttpClient.Builder()
                .sslSocketFactory(getSSLSocketFactory(), getTrustManager())
                .hostnameVerifier((hostname, session) -> true)
                .build();

        Request request = new Request.Builder().url("wss://192.168.1.6:8080").build();
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
    private void playNotificationSound() {
        try {
            MediaPlayer mediaPlayer = MediaPlayer.create(getActivity(), R.raw.notification_sound);
            mediaPlayer.setOnCompletionListener(MediaPlayer::release);
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = requireActivity().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void showNotification(String emotion) {
        // Create notification channel
        createNotificationChannel();

        // Get the NotificationManager from the context
        NotificationManager notificationManager = (NotificationManager) requireContext().getSystemService(Context.NOTIFICATION_SERVICE);

        // Get the patient name from the patientTextView
        String patientName = patientTextView.getText().toString();

        // Build the notification content
        String contentText = patientName + " is " + emotion;

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_logo)
                .setContentTitle("Emotion Received")
                .setContentText(contentText)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setAutoCancel(true); // Removes the notification when tapped

        // Notify
        if (notificationManager != null) {
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        } else {
            Log.e(TAG, "NotificationManager is null");
        }
    }
    private void handleReceivedText(String text) {
        String formattedText = text.trim().toUpperCase();
        Integer drawableResource = emotionMap.get(formattedText);
        getActivity().runOnUiThread(() -> {
            if (drawableResource != null) {
                emotionImage.setImageResource(drawableResource);
                emotionText.setText("is " + formattedText);
                currentEmotion = formattedText;
                List<String> activities = selectRandomActivities(formattedText);
                displayActivities(activities);
                playNotificationSound();

                // Show notification
                createNotificationChannel(); // Ensure the notification channel is created
                showNotification(formattedText);
            } else {
                Log.d(TAG, "Unhandled emotion key: " + formattedText);
            }
        });
    }
    private void showAddActivityDialog() {
        if (currentEmotion == null || currentEmotion.isEmpty()) {
            // Show an alert if no current emotion is detected
            Toast.makeText(getActivity(), "No current emotion detected. Please try again.", Toast.LENGTH_LONG).show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Add Personal Activity for " + currentEmotion);

            final EditText input = new EditText(getActivity());
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

            builder.setPositiveButton("Submit", (dialog, which) -> {
                String activity = input.getText().toString();
                if (!activity.isEmpty()) {
                    savePersonalActivityToDatabase(activity);
                }
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
            builder.show();
        }
    }

    private void savePersonalActivityToDatabase(String activity) {
        if (currentEmotion != null && !currentEmotion.isEmpty()) {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                String path = "PersonalActivities/" + currentUser.getUid() + "/activities/" + currentEmotion.toLowerCase();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference(path);
                ref.push().setValue(activity)
                        .addOnSuccessListener(aVoid -> Toast.makeText(getActivity(), "Activity added", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(getActivity(), "Failed to add activity", Toast.LENGTH_SHORT).show());
            }
        } else {
            Toast.makeText(getActivity(), "No current emotion detected", Toast.LENGTH_SHORT).show();
        }
    }




    private List<String> selectRandomActivities(String emotion) {
        List<String> availableActivities = emotionToActivities.getOrDefault(emotion, new ArrayList<>());
        Collections.shuffle(availableActivities);
        return availableActivities.subList(0, Math.min(3, availableActivities.size()));
    }
}