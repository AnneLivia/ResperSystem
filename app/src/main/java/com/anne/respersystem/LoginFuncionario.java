package com.anne.respersystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListPopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginFuncionario extends AppCompatActivity {

    private FirebaseAuth MinhaAuth;
    // monitora se esta logado ou nao
    private FirebaseAuth.AuthStateListener MinhaAuthListener;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();

    private DatabaseReference ref = database.getReference();

    private boolean achou = false; // variavel de controle se tiver achado cpf ou nao em caso de perda do email

    Button btVoltar, btEntrar;
    TextView tvEsqueciEmail;
    EditText cpf, email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_funcionario);

        btEntrar = (Button) findViewById(R.id.btEntrarConta);
        btVoltar = (Button) findViewById(R.id.btVoltar);

        cpf = (EditText) findViewById(R.id.campocpfentrar);
        email = (EditText) findViewById(R.id.campoEmailEntrar);

        tvEsqueciEmail = (TextView) findViewById(R.id.tvEsqueciEmail);

        tvEsqueciEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(LoginFuncionario.this, R.style.DialogThemeExtra);
                alert.setTitle("Recuperar email");
                final EditText cpf = new EditText(LoginFuncionario.this);

                cpf.setTextColor(Color.WHITE);
                cpf.setTextSize(18);
                cpf.setHint("CPF");
                cpf.setHintTextColor(Color.argb(100, 255, 255, 255));

                // mudando underline color
                ViewCompat.setBackgroundTintList(cpf, ColorStateList.valueOf(Color.GREEN));
                alert.setView(cpf);

                alert.setPositiveButton("Recuperar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // verificar se o cpf nao esta vazio

                        if(!cpf.getText().toString().isEmpty()) {
                            // verificar se o cpf existe no bd, se existir, fornecer email em um dialog, caso contrario, email nao existe
                            ref.child("usuarios").addListenerForSingleValueEvent(new ValueEventListener() {
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot snap : dataSnapshot.getChildren()) {
                                        if (snap.child("cpf").getValue(String.class).equals(cpf.getText().toString())) {
                                            // se algum cpf for igual ao cpf digitado, exibir no dialog
                                            AlertDialog.Builder builder = new AlertDialog.Builder(LoginFuncionario.this, R.style.DialogThemeExtra);
                                            builder.setTitle("Email: " + snap.child("email").getValue().toString());

                                            builder.setNegativeButton("Voltar", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.cancel();
                                                }
                                            });
                                            achou = true;
                                            builder.show();
                                            break;
                                        }
                                    }

                                    if(!achou) {
                                        Toast.makeText(LoginFuncionario.this,"CPF não cadastrado no sistema", Toast.LENGTH_LONG).show();
                                    }
                                    // para buscar outro usuario novamente
                                    achou = false;
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });
                        } else {
                            Toast.makeText(LoginFuncionario.this, "Forneça o CPF", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                alert.show();
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
                    Intent i = new Intent(getApplicationContext(), FuncionarioConectado.class);
                    startActivity(i);
                    LoginFuncionario.this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
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
                finish();
                LoginFuncionario.this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
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
