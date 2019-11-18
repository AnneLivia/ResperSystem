package com.anne.respersystem.ui.home;

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

public class HomeFragment_discente extends Fragment {

    private HomeViewModel_discente homeViewModelDiscente;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModelDiscente =
                ViewModelProviders.of(this).get(HomeViewModel_discente.class);
        View root = inflater.inflate(R.layout.fragment_home_discente, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
        homeViewModelDiscente.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}