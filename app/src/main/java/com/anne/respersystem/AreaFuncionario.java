package com.anne.respersystem;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AreaFuncionario extends AppCompatActivity {
    Button btEntrar, btCadastrar, btVoltar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area_funcionario);

        btEntrar = (Button) findViewById(R.id.btEntrar);
        btCadastrar = (Button) findViewById(R.id.btCadastrar);
        btVoltar = (Button) findViewById(R.id.btVoltar);

        btEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent activityCadastroFuncionario = new Intent(AreaFuncionario.this, LoginFuncionario.class);
                AreaFuncionario.this.startActivity(activityCadastroFuncionario);
                finish();
                AreaFuncionario.this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        btCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent activityCadastroFuncionario = new Intent(AreaFuncionario.this, CadastroFuncionario.class);
                AreaFuncionario.this.startActivity(activityCadastroFuncionario);
                finish();
                AreaFuncionario.this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        btVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent activityVoltarMain = new Intent(AreaFuncionario.this, MainActivity.class);
                AreaFuncionario.this.startActivity(activityVoltarMain);
                finish();
                AreaFuncionario.this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
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
