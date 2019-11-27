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

import com.anne.respersystem.AtualizarReserva;
import com.anne.respersystem.R;

public class HomeFragment_discente extends Fragment {

    private HomeViewModel_discente homeViewModelDiscente;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModelDiscente =
                ViewModelProviders.of(this).get(HomeViewModel_discente.class);
        View root = inflater.inflate(R.layout.fragment_home_discente, container, false);

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        // sempre que iniciar o app, verificar as salas reservadas e remover as que a data de reserva final tenha passado
        AtualizarReserva.atualizarReservas();
    }

    @Override
    public void onStop() {
        super.onStop();
        // sempre que parar o app, verificar as salas reservadas e remover as que a data de reserva final tenha passado
        AtualizarReserva.atualizarReservas();
    }
}