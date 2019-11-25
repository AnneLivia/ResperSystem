package com.anne.respersystem.ui.sobre;

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

public class SobreFragment_discente extends Fragment {

    private SobreViewModel_discente sendViewModelDiscente;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        sendViewModelDiscente =
                ViewModelProviders.of(this).get(SobreViewModel_discente.class);
        View root = inflater.inflate(R.layout.fragment_sobre_discente, container, false);

        return root;
    }
}