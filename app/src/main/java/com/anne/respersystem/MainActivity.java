package com.anne.respersystem;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button btFuncionario, btDiscente;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btFuncionario = (Button) findViewById(R.id.btFuncionario);
        btDiscente = (Button) findViewById(R.id.btDiscente);

        btFuncionario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent activityAreaFuncionario = new Intent(MainActivity.this, AreaFuncionario.class);
                MainActivity.this.startActivity(activityAreaFuncionario);
                finish();
                MainActivity.this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        btDiscente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent activityAreaDiscente = new Intent(MainActivity.this, AreaDiscente.class);
                MainActivity.this.startActivity(activityAreaDiscente);
                finish();
                MainActivity.this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        // sempre que iniciar o app, verificar as salas reservadas e remover as que a data de reserva final tenha passado
        AtualizarReserva.atualizarReservas();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // sempre que parar o app, verificar as salas reservadas e remover as que a data de reserva final tenha passado
        AtualizarReserva.atualizarReservas();
    }
}
