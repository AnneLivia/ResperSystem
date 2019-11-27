package com.anne.respersystem.ui.suporte;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;


import com.anne.respersystem.AtualizarReserva;
import com.anne.respersystem.LoginFuncionario;
import com.anne.respersystem.MainActivity;
import com.anne.respersystem.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class SuporteFragment extends Fragment {

    private FirebaseAuth MinhaAuth;
    // monitora se esta logado ou nao
    private FirebaseAuth.AuthStateListener MinhaAuthListener;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    // obtendo user
    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private DatabaseReference ref = database.getReference();

    private SuporteViewModel shareViewModel;

    private Button btAlterar, btRemoverCadastro;
    private EditText nomeedit, cpfedit, emailedit, telefoneedit;
    private String username, cpfuser, emailuser, telefoneuser, meuuid;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        shareViewModel =
                ViewModelProviders.of(this).get(SuporteViewModel.class);
        View root = inflater.inflate(R.layout.fragment_suporte, container, false);

        nomeedit = (EditText) root.findViewById(R.id.campoSuporteNome);
        cpfedit = (EditText) root.findViewById(R.id.campoSuporteCPF);
        emailedit = (EditText) root.findViewById(R.id.campoSuporteEmail);
        telefoneedit = (EditText) root.findViewById(R.id.campoSuporteTelefone);

        btAlterar = (Button) root.findViewById(R.id.btAlterarCadastro);
        btRemoverCadastro = (Button) root.findViewById(R.id.btRemoverConta);

        btAlterar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // em caso do usuario ter apagado tudo e ter deixado em branco
                if (emailedit.getText().toString().isEmpty() || telefoneedit.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "Preecher todos os campos!", Toast.LENGTH_SHORT).show();
                } else {
                    // algo foi digitado então alterar
                    // reautenticar usuario para poder alterar email
                    // obter credenciais para autenticacao
                    AuthCredential credential = EmailAuthProvider
                            .getCredential(user.getEmail(), cpfuser); // Current Login Credentials
                    user.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Log.d(TAG, "User re-authenticated.");
                                    // alterando o email
                                    user.updateEmail(emailedit.getText().toString())
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Log.d("MeuLog", "Email atualizado");
                                                        ref.child("usuarios").child(user.getUid()).child("email").setValue(emailedit.getText().toString());
                                                        ref.child("usuarios").child(user.getUid()).child("celular").setValue(telefoneedit.getText().toString());

                                                        Toast.makeText(getContext(), "Dados alterados com sucesso!", Toast.LENGTH_SHORT).show();

                                                        // quando email é atualizado, o antigo email fica reservado pelo firebase, caso o dono do email queira usa-lo
                                                        // novamente ao clicar no link de revogação de mudança de email enviado para o antigo email. isso permite que o dono
                                                        // recupere sua conta em caso de hackers, etc.
                                                    }
                                                }
                                            });
                                }
                            });


                }
            }
        });

        btRemoverCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                // coloca para sim
                                try {
                                    // obtendo credenciais para fazer a re autenticacao
                                    AuthCredential credential = EmailAuthProvider
                                            .getCredential(user.getEmail(), cpfuser);
                                    user.reauthenticate(credential)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        // se ele estiver logado, remover sua conta
                                                        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    // remover current user from bd
                                                                    ref.child("usuarios").child(user.getUid()).removeValue();
                                                                    // exibir na tela
                                                                    Toast.makeText(getContext(), "Conta removida com sucesso!", Toast.LENGTH_SHORT).show();
                                                                    Intent i = new Intent(getActivity(), MainActivity.class);
                                                                    getActivity().startActivity(i);
                                                                    getActivity().finish();
                                                                } else {
                                                                    Log.e("TAG", "User account deletion unsucessful.");
                                                                }
                                                            }
                                                        });
                                                    } else {
                                                        Toast.makeText(getContext(), "Falha na autenticação!",
                                                                Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                // nao faca nada
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.DialogThemeTwo);
                builder.setMessage("Deseja remover sua conta?")
                        .setPositiveButton("Sim", dialogClickListener)
                        .setNegativeButton("Não", dialogClickListener).show();
            }
        });

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

        // sempre que iniciar o app, verificar as salas reservadas e remover as que a data de reserva final tenha passado
        AtualizarReserva.atualizarReservas();

        if (user == null) {
            // se usuario estiver vazio, encerra a activity
            getActivity().finish();
        } else {
            ref.child("usuarios").addListenerForSingleValueEvent(new ValueEventListener() {
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snap : dataSnapshot.getChildren()) {
                        if (snap.child("uid").getValue(String.class).equals(user.getUid())) {
                            username = snap.child("nome").getValue(String.class);
                            telefoneuser = snap.child("celular").getValue(String.class);
                            cpfuser = snap.child("cpf").getValue(String.class);
                            emailuser = snap.child("email").getValue(String.class);

                            // colocando nome do funcionario na nav
                            // setando nos editview
                            nomeedit.setText(username);
                            cpfedit.setText(cpfuser);
                            telefoneedit.setText(telefoneuser);
                            emailedit.setText(emailuser);
                            break;
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        // sempre que parar o app, verificar as salas reservadas e remover as que a data de reserva final tenha passado
        AtualizarReserva.atualizarReservas();
    }
}