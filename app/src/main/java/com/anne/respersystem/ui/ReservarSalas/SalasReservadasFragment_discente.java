package com.anne.respersystem.ui.reservarsalas;

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

public class SalasReservadasFragment_discente extends Fragment {

    private SalasReservadasViewModel_discente salasReservadasViewModel_discente;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        salasReservadasViewModel_discente =
                ViewModelProviders.of(this).get(SalasReservadasViewModel_discente.class);
        View root = inflater.inflate(R.layout.fragment_salasreservadas_discente, container, false);
        final TextView textView = root.findViewById(R.id.text_gallery);
        salasReservadasViewModel_discente.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}