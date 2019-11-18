package com.anne.respersystem;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class InserirSala extends AppCompatActivity {

    private FirebaseAuth MinhaAuth;
    // monitora se esta logado ou nao

    Button btSalvarSala, btVoltar;
    EditText nomeSala, localSala, capacidadeSala;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inserir_sala);

        // Buttons
        btSalvarSala = (Button) findViewById(R.id.btSalvarSala);
        btVoltar = (Button) findViewById(R.id.btVoltar);

        // EditView
        nomeSala = (EditText) findViewById(R.id.campoNomeSala);
        localSala = (EditText) findViewById(R.id.campoLocal);
        capacidadeSala = (EditText) findViewById(R.id.campoCapacidade);

        btVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                InserirSala.this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        MinhaAuth = FirebaseAuth.getInstance(); // pegar instania atual do firebase (se usuario logado, login e senha), caso contrario, instacia vazia

        btSalvarSala.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!nomeSala.getText().toString().isEmpty()
                        && !localSala.getText().toString().isEmpty()
                        && !capacidadeSala.getText().toString().isEmpty()) {

                    inserirSalasNoBd(nomeSala.getText().toString(),
                            localSala.getText().toString(), capacidadeSala.getText().toString());
                } else {
                    Toast.makeText(InserirSala.this, "Preencher todos os campos!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    void inserirSalasNoBd(String sala, String local, String capacidade) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        // Cria refefencia chmada usuarios
        // firebaseAuth grtstnst cria id para essa autenticacao
        DatabaseReference ref = database.getReference("salas").
                push();
                            // dentro desse objeto vamos salva o nome do usuario
                ref.child("nome").setValue(sala);
                ref.child("local").setValue(local);
                ref.child("capacidade").setValue(capacidade);
                ref.child("id").setValue(ref.getKey());
                ref.child("defeito").setValue("false");
                Toast.makeText(InserirSala.this, "Sala Inserida com Sucesso!", Toast.LENGTH_SHORT).show();
                finish();
                InserirSala.this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}

