package com.anne.respersystem.ui.send;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.anne.respersystem.R;

public class SendFragment_discente extends Fragment {

    private SendViewModel_discente sendViewModelDiscente;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        sendViewModelDiscente =
                ViewModelProviders.of(this).get(SendViewModel_discente.class);
        View root = inflater.inflate(R.layout.fragment_send_discente, container, false);
        final TextView textView = root.findViewById(R.id.text_send);
        sendViewModelDiscente.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}