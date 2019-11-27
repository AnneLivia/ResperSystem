package com.anne.respersystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CadastroFuncionario extends AppCompatActivity {
    private FirebaseAuth MinhaAuth;
    // monitora se esta logado ou nao
    private FirebaseAuth.AuthStateListener MinhaAuthListener;
    Button btCadastrar, btVoltar;
    EditText email, nomeCompleto, celular, cpf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_funcionario);

        // btCadastrar = (Button) findViewById(R.id.btCadastrarFuncionario); ja chamo no xml
        btVoltar = (Button) findViewById(R.id.btVoltar);

        email = (EditText) findViewById(R.id.campoEmailFuncionario);
        nomeCompleto = (EditText) findViewById(R.id.campoNomeFuncionario);
        cpf = (EditText) findViewById(R.id.campoCPFfuncionario);
        celular = (EditText) findViewById(R.id.campoCelularFuncionario);

        btVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent activityVoltarAreaFuncionario = new Intent(CadastroFuncionario.this, AreaFuncionario.class);
                CadastroFuncionario.this.startActivity(activityVoltarAreaFuncionario);
                finish();
                CadastroFuncionario.this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        MinhaAuth = FirebaseAuth.getInstance(); // pegar instania atual do firebase (se usuario logado, login e senha), caso contrario, instacia vazia
        MinhaAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                // FirebaseAuth é o que vai validar o login do usuario
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null) {
                    // se possuir usuario passa para a proxima activity
                    Log.d("MeuLog", "Usuario conectado: " + user.getUid(), null);
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(i);
                    CadastroFuncionario.this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                } else {
                    Log.d("Meulog", "Usuario não conectado", null);
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        MinhaAuth.addAuthStateListener(MinhaAuthListener);
        AtualizarReserva.atualizarReservas();
    }

    @Override
    public void onStop() {
        super.onStop();
        MinhaAuth.removeAuthStateListener(MinhaAuthListener);
        AtualizarReserva.atualizarReservas();
    }

    public void cadastrarUsuario(View v) {
        if (!email.getText().toString().equals("") && !cpf.getText().toString().equals("") &&
                !celular.getText().toString().equals("") && !nomeCompleto.getText().toString().equals("")) {
            MinhaAuth.createUserWithEmailAndPassword(email.getText().toString(), cpf.getText().toString()).addOnCompleteListener(
                    this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(CadastroFuncionario.this, "Email já cadastrado ou Senha muito curta!", Toast.LENGTH_SHORT).show();
                                Log.d("MeuLog", "Cadastro errado " + task.getException().getMessage());
                            } else {
                                // Criar firebase databse
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                // Cria refefencia chmada usuarios
                                // firebaseAuth grtstnst cria id para essa autenticacao
                                DatabaseReference ref = database.getReference("usuarios").
                                        child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                // dentro desse objeto vamos salva o nome do usuario
                                ref.child("nome").setValue(nomeCompleto.getText().toString());
                                ref.child("uid").setValue(FirebaseAuth.getInstance().getUid());
                                ref.child("email").setValue(email.getText().toString());
                                ref.child("cpf").setValue(cpf.getText().toString());
                                ref.child("celular").setValue(celular.getText().toString());
                                ref.child("aprovado").setValue(false);
                                Toast.makeText(CadastroFuncionario.this, "Cadastrado com successo!", Toast.LENGTH_SHORT).show();
                                FirebaseAuth.getInstance().signOut();
                                // mover para tela de bem-vindo
                                Intent t = new Intent(CadastroFuncionario.this, Welcome.class);
                                CadastroFuncionario.this.startActivity(t);
                                finish();
                            }
                        }
                    });
        } else {
            Toast.makeText(CadastroFuncionario.this, "Preencher todos os campos para efetuar o login!", Toast.LENGTH_SHORT).show();
        }
    }
}
