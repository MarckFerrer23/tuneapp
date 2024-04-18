package com.example.tuneapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

public class EmotionFragment extends Fragment {
    private WebSocketViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_emotion, container, false);
        viewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getActivity().getApplication())).get(WebSocketViewModel.class);

        initializeEmotionButtons(view);
        return view;
    }

    private void initializeEmotionButtons(View view) {
        Button buttonHappy = view.findViewById(R.id.buttonHappy);
        buttonHappy.setOnClickListener(v -> viewModel.sendEmotion("HAPPY"));

        Button buttonSad = view.findViewById(R.id.buttonSad);
        buttonSad.setOnClickListener(v -> viewModel.sendEmotion("SAD"));

        Button buttonAngry = view.findViewById(R.id.buttonAngry);
        buttonAngry.setOnClickListener(v -> viewModel.sendEmotion("ANGRY"));

        Button buttonFear = view.findViewById(R.id.buttonFear);
        buttonFear.setOnClickListener(v -> viewModel.sendEmotion("FEAR"));
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        viewModel.onPause();
    }
}
