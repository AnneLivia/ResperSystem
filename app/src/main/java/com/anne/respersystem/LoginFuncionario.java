package com.anne.respersystem;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListPopupWindow;

public class LoginFuncionario extends AppCompatActivity {
    Button btVoltar, btEntrar;
    EditText cpf;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_funcionario);

        btEntrar = (Button) findViewById(R.id.btEntrarConta);
        btVoltar = (Button) findViewById(R.id.btVoltar);

        cpf = (EditText) findViewById(R.id.campocpfentrar);


        btVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent activityVoltarMain = new Intent(LoginFuncionario.this, AreaFuncionario.class);
                LoginFuncionario.this.startActivity(activityVoltarMain);
                //finish();
            }
        });
    }
}
