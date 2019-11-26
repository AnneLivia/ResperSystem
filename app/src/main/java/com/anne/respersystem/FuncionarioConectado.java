package com.anne.respersystem;

import android.content.DialogInterface;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.TextView;

public class FuncionarioConectado extends AppCompatActivity {

    private FirebaseAuth MinhaAuth;
    // monitora se esta logado ou nao
    private FirebaseAuth.AuthStateListener MinhaAuthListener;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();

    private DatabaseReference ref = database.getReference();

    private AppBarConfiguration mAppBarConfiguration;

    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_funcionario_conectado);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_salasdisponiveis, R.id.nav_reservarsalas,
                R.id.nav_calendar, R.id.nav_suporte, R.id.nav_sobre)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.logout) {
            FirebaseAuth.getInstance().signOut();
            finish();
            FuncionarioConectado.this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // obtendo nome

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user == null) {
            // se usuario estiver vazio, encerra a activity
            finish();
        } else {
            ref.child("usuarios").addListenerForSingleValueEvent(new ValueEventListener() {
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot snap : dataSnapshot.getChildren()) {
                        if (snap.child("uid").getValue(String.class).equals(user.getUid())) {
                            name = snap.child("nome").getValue(String.class);
                            // verificar se o login do usuario já foi aprovado ou não
                            Boolean aprovou = snap.child("aprovado").getValue(Boolean.class);
                            if(!aprovou) {
                                // se for false é porque o admin ainda não aprovou o login do usuário
                                AlertDialog.Builder alert = new AlertDialog.Builder(FuncionarioConectado.this, R.style.DialogThemeExtra);
                                // para não permitir que o usuario feche o dialog sem apertar o ok
                                alert.setCancelable(false);

                                alert.setTitle("Atenção");
                                alert.setMessage("Seu cadastro ainda não foi aprovado pelo Admin. Se ainda não tiver enviado seu" +
                                        " documento de comprovação de vínculo com a UFPA, por favor, enviar para o email: adminresper@gmail.com");

                                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        finish();
                                        // finalizar login do usuario
                                        FirebaseAuth.getInstance().signOut();
                                    }
                                });

                                alert.show();

                            }

                            // colocando nome do funcionario na nav
                            NavigationView nw = (NavigationView) findViewById(R.id.nav_view);
                            View hView = nw.getHeaderView(0);
                            TextView nav_user = (TextView) hView.findViewById(R.id.funcionario_nome);
                            nav_user.setText(name);
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
}
