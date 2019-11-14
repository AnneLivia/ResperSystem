package com.anne.respersystem;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class CadastroFuncionario extends AppCompatActivity {
    Button btArquivo, btCadastrar, btVoltar;
    EditText email, nomeCompleto, celular;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_funcionario);

        btArquivo = (Button) findViewById(R.id.btInserirComprovante);
        btCadastrar = (Button) findViewById(R.id.btCadastrar);
        btVoltar = (Button) findViewById(R.id.btVoltar);


        btVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent activityVoltarAreaFuncionario = new Intent(CadastroFuncionario.this, AreaFuncionario.class);
                CadastroFuncionario.this.startActivity(activityVoltarAreaFuncionario);
                finish();
            }
        });

    }
}
