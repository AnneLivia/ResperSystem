package com.anne.respersystem.ui.suporte;

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

public class ShareFragment_discente extends Fragment {

    private ShareViewModel_discente shareViewModelDiscente;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        shareViewModelDiscente =
                ViewModelProviders.of(this).get(ShareViewModel_discente.class);
        View root = inflater.inflate(R.layout.fragment_share_discente, container, false);
        final TextView textView = root.findViewById(R.id.text_share);
        shareViewModelDiscente.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}