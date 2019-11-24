package com.anne.respersystem;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class ReservarSala extends AppCompatActivity {

    private FirebaseAuth MinhaAuth;
    // monitora se esta logado ou nao

    Button btReservaSala, btVoltar, campoDataHoraInicial, campoDataHoraFinal;
    EditText campoConfSala, campoEvento, campoDescricao, campoSite;

    TextView campoFuncionario;

    //data e hora inicial
    Calendar calendarioinicial;
    DatePickerDialog PegarDatainicialDlg;
    TimePickerDialog PegarHorainicialDlg;

    //data e hora final
    Calendar calendariofinal;
    DatePickerDialog PegarDatafinalDlg;
    TimePickerDialog PegarHorafinalDlg;
    //private ArrayList<String> sala;

    String nameFuncionario;

    //Array para recerber a sala
    private ArrayList<String> salareservada;
    private ArrayList<String> isComDefeito;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference ref = database.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservar_sala);

        //Buttons
        btReservaSala = (Button) findViewById(R.id.btReservaSala);
        btVoltar = (Button) findViewById(R.id.btVoltar);

        //EditText

        campoConfSala = (EditText) findViewById(R.id.campoConfSala);
        campoEvento = (EditText) findViewById(R.id.campoEvento);
        campoDescricao = (EditText) findViewById(R.id.campoDescricao);
        campoDataHoraInicial = (Button) findViewById(R.id.campoDataHoraInicial);
        campoDataHoraFinal = (Button) findViewById(R.id.campoDataHoraFinal);
        campoSite = (EditText) findViewById(R.id.campoSite);
        //campoFuncionario = (TextView) findViewById(R.id.campoFuncionario);



        btVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                ReservarSala.this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

            }
        });


        MinhaAuth = FirebaseAuth.getInstance(); // instancia atual do firebase (usuario logado, login e senha), caso contrario, instancia vazia

        btReservaSala.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String campo = campoConfSala.getText().toString();  //recebe para comparar, bugava ao inserir direto
                boolean receptor = true; //+ break controla o toast sala inexistente para não fica repetindo na tela
                //laço for para puxa os dados do ArrayList
                for(int i = 0; i < salareservada.size(); i++){
                    if(campo.equals(salareservada.get(i))){
                        // Checar se a sala nao esta com defeito, caso esteja alertar o usuario
                        if(isComDefeito.get(i).equals("false")) {
                            if (!campoConfSala.getText().toString().isEmpty()
                                    && !campoEvento.getText().toString().isEmpty()
                                    && !campoDataHoraInicial.getText().toString().isEmpty()
                                    && !campoDataHoraFinal.getText().toString().isEmpty()) {

                                inserirSalasreservadasNoBd(campoConfSala.getText().toString(),
                                        nameFuncionario.toString(), campoEvento.getText().toString(), campoDescricao.getText().toString(),
                                        campoDataHoraInicial.getText().toString(), campoDataHoraFinal.getText().toString(),
                                        campoSite.getText().toString());
                                receptor = true;
                                break;

                            } else {
                                Toast.makeText(ReservarSala.this, "Preencher todos os campos!", Toast.LENGTH_SHORT).show();
                                receptor = true;
                                break;
                            }
                        } else {
                            Toast.makeText(ReservarSala.this, "Sala está com defeito ou passando por reformas!", Toast.LENGTH_SHORT).show();
                            receptor = true;
                            break;
                        }
                    } else{
                        receptor = false;
                    }
                }

                if(!receptor == true){
                    Toast.makeText(ReservarSala.this, "Sala Inexistente! Verifique o nome!", Toast.LENGTH_SHORT).show();
                }
                }

        });
    }


    void inserirSalasreservadasNoBd(String nomesala, String nomefuncionario, String nomeEvento,
                                    String nomeDescricao, String DHInicial, String DHFinal, String nomeSite) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        // Cria refefencia chmada usuarios
        // firebaseAuth grtstnst cria id para essa autenticacao
        DatabaseReference ref = database.getReference("salas_reservadas").
                push();

        ref.child("Sala").setValue(nomesala);
        ref.child("Funcionario").setValue(nomefuncionario);
        ref.child("uid").setValue(MinhaAuth.getUid());
        ref.child("Evento").setValue(nomeEvento);
        ref.child("Descricao").setValue(nomeDescricao);
        ref.child("Site").setValue(nomeSite);
        ref.child("Data_Inicial").setValue(DHInicial);
        ref.child("Data_Final").setValue(DHFinal);
        ref.child("id").setValue(ref.getKey());

        Toast.makeText(ReservarSala.this, "Sala Reservada com Sucesso!", Toast.LENGTH_SHORT).show();
        finish();
        ReservarSala.this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void PegarDataInicial(View view) {
        campoDataHoraInicial = findViewById(R.id.campoDataHoraInicial);
        calendarioinicial = Calendar.getInstance();
        int dia = calendarioinicial.get(Calendar.DAY_OF_MONTH);
        int mes = calendarioinicial.get(Calendar.MONTH);
        int ano = calendarioinicial.get(Calendar.YEAR);

        PegarDatainicialDlg = new DatePickerDialog(ReservarSala.this, R.style.DialogCalendarTheme, new DatePickerDialog.OnDateSetListener() {
            //final Calendar myCalendar = Calendar.getInstance();
            @Override
            public void onDateSet(DatePicker view, int year,
                                  int monthOfYear, int dayOfMonth) {
                // TODO Auto-generated method stub
                calendarioinicial.set(Calendar.YEAR, year);
                calendarioinicial.set(Calendar.MONTH, monthOfYear);
                calendarioinicial.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "dd-MM-yyyy"; //In which you need put here
                Locale local = new Locale("pt", "BR");
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, local);

                // para quando tiver um numero com 0 na frente, ele aparecer
                String datatextformatted = String.format("%02d/%02d/%04d", dayOfMonth, monthOfYear + 1, year);

                campoDataHoraInicial.setText(datatextformatted);
                PegarHoraInicial(view);
                //   expiry.setText(sdf.format(myCalendar.getTime()));

            }
        }, dia, mes, ano);
        PegarDatainicialDlg.getDatePicker().setMinDate(System.currentTimeMillis());
        PegarDatainicialDlg.show();
    }

    public void PegarHoraInicial(View view) {
        campoDataHoraInicial = findViewById(R.id.campoDataHoraInicial);
        calendarioinicial = Calendar.getInstance();
        int hora = calendarioinicial.get(Calendar.HOUR_OF_DAY);
        int minutos = calendarioinicial.get(Calendar.MINUTE);

        PegarHorainicialDlg = new TimePickerDialog(ReservarSala.this, R.style.DialogCalendarTheme, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                // para quando tiver um numero com 0 na frente, ele aparecer
                String horatextformatted = String.format("%02d:%02d", hourOfDay, minute);
                campoDataHoraInicial.setText(campoDataHoraInicial.getText() + " " + horatextformatted);
            }
        }, hora, minutos,true);
        PegarHorainicialDlg.show();
    }

    public void PegarDataFinal(View view) {
        campoDataHoraFinal = findViewById(R.id.campoDataHoraFinal);
        calendariofinal = Calendar.getInstance();
        int dia = calendariofinal.get(Calendar.DAY_OF_MONTH);
        int mes = calendariofinal.get(Calendar.MONTH);
        int ano = calendariofinal.get(Calendar.YEAR);

        PegarDatafinalDlg = new DatePickerDialog(ReservarSala.this, R.style.DialogCalendarTheme,new DatePickerDialog.OnDateSetListener() {
            //final Calendar myCalendar = Calendar.getInstance();
            @Override
            public void onDateSet(DatePicker view, int year,
                                  int monthOfYear, int dayOfMonth) {
                // TODO Auto-generated method stub
                calendariofinal.set(Calendar.YEAR, year);
                calendariofinal.set(Calendar.MONTH, monthOfYear);
                calendariofinal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "dd-MM-yyyy"; //In which you need put here
                Locale local = new Locale("pt", "BR");
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, local);

                // para quando tiver um numero com 0 na frente, ele aparecer
                String datatextformatted = String.format("%02d/%02d/%04d", dayOfMonth, monthOfYear + 1, year);

                campoDataHoraFinal.setText(datatextformatted);
                PegarHoraFinal(view);
                //   expiry.setText(sdf.format(myCalendar.getTime()));

            }
        }, dia, mes, ano);
        PegarDatafinalDlg.getDatePicker().setMinDate(System.currentTimeMillis());
        PegarDatafinalDlg.show();
    }

    public void PegarHoraFinal(View view) {
        campoDataHoraFinal = findViewById(R.id.campoDataHoraFinal);
        calendariofinal = Calendar.getInstance();
        int hora = calendariofinal.get(Calendar.HOUR_OF_DAY);
        int minutos = calendariofinal.get(Calendar.MINUTE);

        PegarHorafinalDlg = new TimePickerDialog(ReservarSala.this, R.style.DialogCalendarTheme, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                // para quando tiver um numero com 0 na frente, ele aparecer
                String horatextformatted = String.format("%02d:%02d", hourOfDay, minute);
                campoDataHoraFinal.setText(campoDataHoraFinal.getText() + " " + horatextformatted);
            }
        }, hora, minutos,true);
        PegarHorafinalDlg.show();
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
                            nameFuncionario = snap.child("nome").getValue(String.class);
                            // colocando nome do funcionario na nav
                            //NavigationView nw = (NavigationView) findViewById(R.id.nav_view);
                            //View hView = nw.getHeaderView(0);
                            TextView nav_user = (TextView) findViewById(R.id.campoFuncionario);
                            nav_user.setText(nameFuncionario);
                            break;
                        }
                    }
                }


                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });



        }

        //Recebe nome da sala
        salareservada = new ArrayList<String>();
        isComDefeito = new ArrayList<String>();
        ref.child("salas").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dt : dataSnapshot.getChildren()) {
                    String SR = dt.child("nome").getValue(String.class);
                    salareservada.add(SR);
                    // para verificar se a sala nao esta com defeito
                    isComDefeito.add(dt.child("defeito").getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

}
