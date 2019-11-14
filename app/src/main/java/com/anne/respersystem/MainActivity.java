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
            }
        });

    }
}
