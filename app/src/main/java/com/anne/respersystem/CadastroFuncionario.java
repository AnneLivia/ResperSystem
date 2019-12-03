package com.anne.respersystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
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
    CheckBox termos;
    TextView termosdeusoTexto;

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
        termos = (CheckBox) findViewById(R.id.checkeTermosDeUso);
        termosdeusoTexto = (TextView) findViewById(R.id.tvLinkTermosdeUso);

        // para abrir os termos de uso
        termosdeusoTexto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CadastroFuncionario.this, R.style.CalenderViewCustom);
                builder.setTitle("Termos de Uso");
                builder.setMessage("ANTES DE SE ASSOCIAR AO SITE LEIA CUIDADOSAMENTE ESTE TERMO E CONDIÇÕES DE USO DO SERVIÇO.\n" +
                        "\n" +
                        "Ao efetuar o registro o USUÁRIO ESTARÁ DECLARANDO TER LIDO E ACEITO INTEGRALMENTE, SEM QUALQUER RESERVA, " +
                        "ESTE CONTRATO e TERMO DE USO que apresenta as “Condições Gerais” aplicáveis ao uso dos serviços " +
                        "oferecidos por Simples Loja Online, um produto Tuiuiú Tecnologia da Informação, sociedade limitada, " +
                        "inscrita no CNPJ sob n.º 08638806/0001-11, " +
                        "doravante denominado “Simples Loja Online”, que presta um serviço interativo de prestação " +
                        "de serviços na internet, fornecendo uma plataforma virtual, locando espaços virtuais e " +
                        "hospedando conteúdo fornecido por terceiros. O Simples Loja Online é um serviço que " +
                        "disponibiliza ferramentas de lojas virtuais para diversos empreendedores na web, " +
                        "onde os visitantes livremente escolhem uma loja em que pretendem adquirir produtos " +
                        "e serviços de seu interesse. Portanto, o Simples Loja Online não realiza vendas, " +
                        "não faz intermediações, não oferece produtos ou serviços próprios. " +
                        "Os serviços prestados são operados e administrados por Simples " +
                        "Loja Online na rede mundial de computadores (“Internet”), " +
                        "dentro do site www.simpleslojaonline.com.br e demais plataformas formalmente filiadas.");

                builder.setNegativeButton("Voltar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

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
        if(termos.isChecked()) {
            if (!email.getText().toString().equals("") && isCPF(cpf.getText().toString()) &&
                    !celular.getText().toString().equals("") && !nomeCompleto.getText().toString().equals("") &&
                    !isOnlySpace(nomeCompleto.getText().toString())) {
                MinhaAuth.createUserWithEmailAndPassword(email.getText().toString(), cpf.getText().toString()).addOnCompleteListener(
                        this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    // identificar o tipo de erro que ocorreu
                                    String typeOfError = task.getException().getMessage();
                                    // erro de email
                                    if (typeOfError.equals("The email address is already in use by another account.")) {
                                        Toast.makeText(CadastroFuncionario.this, "Email já cadastrado no sistema.", Toast.LENGTH_SHORT).show();
                                    } else if (typeOfError.equals("The given password is invalid. [ Password should be at least 6 characters ]")) {
                                        // erro de senha (CPF)
                                        Toast.makeText(CadastroFuncionario.this, "Senha muito curta. Por favor, digitar todos os digitos de seu CPF.", Toast.LENGTH_SHORT).show();
                                    } else if (typeOfError.equals("An internal error has occurred. [ 7: ]")) {
                                        // erro de conexão
                                        Toast.makeText(CadastroFuncionario.this, "Falha ao cadastrar, provavelmente causado por sua conexão com a internet.", Toast.LENGTH_SHORT).show();
                                    } else if (typeOfError.equals("The email address is badly formatted.")) {
                                        email.setError("Forneça um endereço de email válido.");
                                        email.requestFocus();
                                    }
                                    // Toast.makeText(CadastroFuncionario.this, "Email já cadastrado ou Senha muito curta!", Toast.LENGTH_SHORT).show();
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
                if (!isCPF(cpf.getText().toString())) {
                    cpf.setError("Forneça um CPF válido e sem símbolos.");
                    // o popup do erro só aparece quando o edittext está em focus. então colocar focus nele automaticamente para o popup aparecer
                    cpf.requestFocus();
                }

                if (celular.getText().toString().isEmpty() || celular.getText().toString().equals("")) {
                    celular.setError("Forneça seu número de celular");
                    celular.requestFocus();
                }

                if (email.getText().toString().isEmpty() || email.getText().toString().equals("")) {
                    email.setError("Forneça seu email.");
                    email.requestFocus();
                }

                if (nomeCompleto.getText().toString().isEmpty() || nomeCompleto.getText().toString().equals("")
                        || isOnlySpace(nomeCompleto.getText().toString())) {
                    nomeCompleto.setError("Insira seu nome.");
                    nomeCompleto.requestFocus();
                }
            }
        } else {
            Toast.makeText(CadastroFuncionario.this, "Aceite os termos de uso para poder se cadastrar.", Toast.LENGTH_SHORT).show();
        }
    }

    // função para checar se o CPF está no formato valido
    boolean isCPF(String cpf) {
        if(cpf.length() != 11) {
          return false; // cpf tem 11 digitos sem qualquer simbolo
        } else {
            for (int i = 0; i < cpf.length(); i++) {
                // se o char atual não for igual a um número, então é simbolo, retornar false
                if(!(cpf.charAt(i) >= '0' && cpf.charAt(i) <= '9')) {
                    return false;
                }
            }
        }
        // se chegou aqui é porque é cpf
        return true;
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
}
