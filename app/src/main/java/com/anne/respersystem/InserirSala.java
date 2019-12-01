package com.anne.respersystem;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

        // para verificar se usuario está conectado ou não
        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    //Toast.makeText(getApplicationContext(), "Conectado.", Toast.LENGTH_SHORT).show();
                } else {
                    // se não entiver volta
                    Toast.makeText(getApplicationContext(), "Sem conexão.", Toast.LENGTH_SHORT).show();
                    finish();
                    InserirSala.this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled");
            }
        });

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
                        && !isOnlySpace(nomeSala.getText().toString())
                        && !localSala.getText().toString().isEmpty()
                        && !isOnlySpace(localSala.getText().toString())
                        && !capacidadeSala.getText().toString().isEmpty()
                        && !isOnlySpace(capacidadeSala.getText().toString())) {

                    inserirSalasNoBd(nomeSala.getText().toString(),
                            localSala.getText().toString(), capacidadeSala.getText().toString());
                } else {
                    if(nomeSala.getText().toString().isEmpty() || isOnlySpace(nomeSala.getText().toString())) {
                        nomeSala.setError("Insira o nome da sala.");
                        nomeSala.requestFocus();
                    }
                    if(localSala.getText().toString().isEmpty() || isOnlySpace(localSala.getText().toString())) {
                        localSala.setError("Insira o local da sala.");
                        localSala.requestFocus();
                    }
                    if(capacidadeSala.getText().toString().isEmpty() || isOnlySpace(capacidadeSala.getText().toString())) {
                        capacidadeSala.setError("Insira a capacidade da sala.");
                        capacidadeSala.requestFocus();
                    }

                    Toast.makeText(InserirSala.this, "Necessário preencher todos os campos!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    boolean isOnlySpace(String s) {
        int count = 0;
        for (int i = 0; i < s.length(); i++) {
            // se o char atual não for igual a um número, então é simbolo, retornar false
            if(s.charAt(i) == ' ') {
                count++;
            }
        }
        // se o valor em count for do mesmo tamanho da string é porque só existe espaço
        if(count == s.length())
            return true;
        return false;
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
                ref.child("uid").setValue(MinhaAuth.getUid());
                Toast.makeText(InserirSala.this, "Sala Inserida com Sucesso!", Toast.LENGTH_SHORT).show();
                finish();
                InserirSala.this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
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

