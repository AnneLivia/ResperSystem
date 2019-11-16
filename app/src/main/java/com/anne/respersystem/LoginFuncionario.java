package com.anne.respersystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListPopupWindow;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginFuncionario extends AppCompatActivity {

    private FirebaseAuth MinhaAuth;
    // monitora se esta logado ou nao
    private FirebaseAuth.AuthStateListener MinhaAuthListener;

    Button btVoltar, btEntrar;
    EditText cpf, email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_funcionario);

        btEntrar = (Button) findViewById(R.id.btEntrarConta);
        btVoltar = (Button) findViewById(R.id.btVoltar);

        cpf = (EditText) findViewById(R.id.campocpfentrar);
        email = (EditText) findViewById(R.id.campoEmailEntrar);

        MinhaAuth = FirebaseAuth.getInstance(); // pegar instania atual do firebase (se usuario logado, login e senha), caso contrario, instacia vazia
        MinhaAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                // FirebaseAuth é o que vai validar o login do usuario
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null) {
                    // se possuir usuario passa para a proxima activity
                    Log.d("MeuLog", "Usuario conectado: " + user.getUid(), null);
                    Intent i = new Intent(getApplicationContext(), FuncionarioConectado.class);
                    startActivity(i);
                } else {
                    Log.d("Meulog", "Usuario não conectado", null);
                }
            }
        };


        btVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent activityVoltarMain = new Intent(LoginFuncionario.this, AreaFuncionario.class);
                LoginFuncionario.this.startActivity(activityVoltarMain);
                //finish();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        MinhaAuth.addAuthStateListener(MinhaAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        MinhaAuth.removeAuthStateListener(MinhaAuthListener);
    }

    public void loginFuncionario(View v) {
        if (!email.getText().toString().equals("") && !cpf.getText().toString().equals("")) {
            MinhaAuth.signInWithEmailAndPassword(email.getText().toString(),
                    cpf.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        // se nao foi um sucesso
                        Log.d("MeuLog", "Login nao realizado");
                        Toast.makeText(LoginFuncionario.this, "Login Invalido!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LoginFuncionario.this, "Bem vindo (a)!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(LoginFuncionario.this, "Preencher campos para o login", Toast.LENGTH_SHORT).show();
        }
    }
}
