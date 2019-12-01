package com.anne.respersystem;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class ReservarSala extends AppCompatActivity {

    private FirebaseAuth MinhaAuth;
    // monitora se esta logado ou nao

    Button btReservaSala, btVoltar, campoDataHoraInicial, campoDataHoraFinal;
    //EditText campoConfSala, campoEvento, campoDescricao, campoSite;
    EditText campoEvento, campoDescricao, campoSite;

    AutoCompleteTextView campoConfSala;
    ArrayAdapter<String> adapter;

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

    // para receber as salas que já foram reservadas
    ArrayList<Pair<String, Pair<String, String> > > jaforamreservadas; // o map guardara na chave a sala e no pair, ele guardara o inicio e o fim

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference ref = database.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservar_sala);

        //Buttons
        btReservaSala = (Button) findViewById(R.id.btReservaSala);
        btVoltar = (Button) findViewById(R.id.btVoltar);

        //AutoComplete text para mostrar dropdown
        campoConfSala = (AutoCompleteTextView) findViewById(R.id.campoConfSala);

        // para exibir opções ao clicar em cima do edittext
        campoConfSala.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event){
                campoConfSala.showDropDown();
                return false;
            }
        });

        campoEvento = (EditText) findViewById(R.id.campoEvento);
        campoDescricao = (EditText) findViewById(R.id.campoDescricao);
        campoDataHoraInicial = (Button) findViewById(R.id.campoDataHoraInicial);
        campoDataHoraFinal = (Button) findViewById(R.id.campoDataHoraFinal);
        campoSite = (EditText) findViewById(R.id.campoSite);
        //campoFuncionario = (TextView) findViewById(R.id.campoFuncionario);

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
                    ReservarSala.this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
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
                                    && !campoDataHoraFinal.getText().toString().isEmpty()
                                    && !campoDataHoraInicial.getText().toString().equalsIgnoreCase("DATA/HORA FINAL")
                                    && !campoDataHoraFinal.getText().toString().equalsIgnoreCase("DATA/HORA INICIAL")
                                    && campoDataHoraInicial.getText().toString().length() == 16
                                    && campoDataHoraFinal.getText().toString().length() == 16 ) {

                                // verificar também se não é igual a DATA/HORA FINAL e DATA/HORA INICIAL pois caso
                                // o usuario não entre com uma data, ainda sim, haverá esse valor lá
                                // o tamanho de campodahorainicial deve ser 16 porque se for menor é
                                // porque o usuario não inseriu hora e data completo

                                // obter somente data inicial e final e somente hora inicial e final separados
                                String horainicial = "", horafinal = "", dinicial = "", dfinal = "";
                                boolean isdate = false; // usada para indicar momento de pegar somente hora
                                for(int j = 0; j < campoDataHoraInicial.getText().toString().length(); j++) {
                                    if(campoDataHoraInicial.getText().toString().charAt(j) != ' ' && !isdate) {
                                        dinicial+=campoDataHoraInicial.getText().toString().charAt(j);
                                        dfinal+=campoDataHoraFinal.getText().toString().charAt(j);
                                    } else {
                                        isdate = true;
                                        // assim que chegar aqui, será um espaço, necessario verificar isso para não coloca-lo na string
                                        if(campoDataHoraInicial.getText().toString().charAt(j) != ' ') {
                                            horainicial+=campoDataHoraInicial.getText().toString().charAt(j);
                                            horafinal+=campoDataHoraFinal.getText().toString().charAt(j);
                                        }
                                    }
                                }

                                // Transformar data inicial e final para Date
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

                                Date datainicial = new Date(), datafinal = new Date();
                                Date datainicialreservada = new Date(), datafinalreservada = new Date();

                                try {
                                    datainicial = sdf.parse(dinicial);
                                    datafinal = sdf.parse(dfinal);
                                } catch (ParseException pex){
                                    pex.printStackTrace();
                                }

                                String dinicialreservada = "",
                                        dfinalreservada = "",
                                        horainicialreservada = "",
                                        horafinalreservada = "";

                                // para determinar se a sala poderá ser reservada ou não
                                Boolean podeReservar = true;
                                for (Pair<String, Pair<String, String> > ress : jaforamreservadas) {
                                    // exibindo na tela as salas que ja foram reservadas
                                    dinicialreservada = "";
                                    dfinalreservada = "";
                                    horainicialreservada = "";
                                    horafinalreservada = "";

                                    // obtendo data e hora separada para o já reservado também
                                    isdate = false;
                                    for(int j = 0; j < ress.second.first.length(); j++) {
                                        if(ress.second.first.charAt(j) != ' ' && !isdate) {
                                            dinicialreservada+=ress.second.first.charAt(j);
                                            dfinalreservada+=ress.second.second.charAt(j);
                                        } else {
                                            isdate = true;
                                            // assim que chegar aqui, será um espaço, necessario verificar isso para não coloca-lo na string
                                            if(ress.second.first.charAt(j) != ' ') {
                                                horainicialreservada+=ress.second.first.charAt(j);
                                                horafinalreservada+=ress.second.second.charAt(j);
                                            }
                                        }
                                    }

                                    try {
                                        datainicialreservada = sdf.parse(dinicialreservada);
                                        datafinalreservada = sdf.parse(dfinalreservada);
                                    } catch (ParseException pex){
                                        pex.printStackTrace();
                                    }

                                    // primeiro veriricar se a sala que foi reservada e igual a analisada
                                    if (ress.first.equals(campoConfSala.getText().toString())) {
                                        // O trecho de codigo abaixo serve para verificar as datas
                                        // primeiro cenário se o evento ocorrer em apenas 1 dia, o que poderá desempatar é a hora, mas.
                                        if(datainicial.equals(datafinal)) {
                                            // primeiro verificar se data inicial é diferente do inicial reservada e final reservada
                                            // se for diferente, é garantido que o eventos reservado para a sala atual do loop não ocorre em um só dia
                                            if(!datainicial.equals(datainicialreservada) && !datainicial.equals(datafinalreservada)) {
                                                // verficar se a data é maior que a datafinalreservada ou se é menor que a datainicialreservada
                                                if(datainicial.compareTo(datafinalreservada) > 0 || datainicial.compareTo(datainicialreservada) < 0) {
                                                    podeReservar = true; // pode reservar pois datas não estão no mesmo intervalo neste caso
                                                } else {
                                                    podeReservar = false; // por ser apenas um dia é garantido que a sala ja esta reservada para o dia
                                                    break;
                                                }
                                            } else {
                                                // verificar com qual data o dia escolhido é igual, se é a final do evento ou a inicial
                                                // caso o dia que já foi reservado seja igual também. Verificar para a hora, a mesma coisa que para a data acima
                                                if(datainicialreservada.equals(datafinalreservada) && datainicial.equals(datainicialreservada)) {
                                                    if(horainicial.compareTo(horafinalreservada) > 0 || horafinal.compareTo(horainicialreservada) < 0) {
                                                        podeReservar = true;
                                                    } else {
                                                        podeReservar = false;
                                                        break;
                                                    }
                                                } else {
                                                    // supoe-se que a datainicial reservada é diferente da finalreservada
                                                    if(datainicial.equals(datainicialreservada)) {
                                                        // verificar se hora final do evento é menor que a hora inicial reservada
                                                        if(horafinal.compareTo(horainicialreservada) < 0) {
                                                            podeReservar = true;
                                                        } else {
                                                            podeReservar = false;
                                                            break;
                                                        }
                                                    } else {
                                                        // é igual a final, verificar se a hora inicial é apos a hora final reservada
                                                        if(horainicial.compareTo(horafinalreservada) > 0) {
                                                            podeReservar = true;
                                                        } else {
                                                            podeReservar = false;
                                                            break;
                                                        }
                                                    }
                                                }
                                            }
                                        } else {
                                            // se entrar neste else, o eventos ocorre durante vários dias
                                            if(datafinal.compareTo(datainicialreservada) < 0 || datainicial.compareTo(datafinalreservada) > 0) {
                                                // se entrar aqui, é porque evento ocorre antes ou depois do tempo que foi reservado
                                                podeReservar = true;
                                            } else {
                                                // uma das datas são iguais a data reservada (vericar a hora)
                                               if(datainicial.equals(datafinalreservada)) {
                                                    // a datainicial só pode ser igual a final, se for igual a inicial haverá sobreposição de horários
                                                    // a datafinal só pode ser igual a inicial, se for igual a final, haverá sobreposição também
                                                    // quando levado em consideração que as datas são diferentes também para a reservada
                                                    // e por levar em consideração que o evento ocorre durante mais de um dia
                                                    // se o evento começar após a duração da sala reservada, pode reservar então
                                                    if(horainicial.compareTo(horafinalreservada) > 0) {
                                                        podeReservar = true;
                                                    } else {
                                                        podeReservar = false;
                                                        break;
                                                    }
                                                } else if (datafinal.equals(datainicialreservada)){
                                                    // se a hora final do evento acabar antes da proxima reserva
                                                    if(horafinal.compareTo(horainicialreservada) < 0) {
                                                        podeReservar = true;
                                                    } else {
                                                        podeReservar = false;
                                                        break;
                                                    }
                                                } else {
                                                   // não tem dias iguais e é garantido que os tempos estão se sobreescrevendo
                                                   podeReservar = false;
                                                   break;
                                               }
                                            }
                                        }
                                    }
                                }

                                // todos os false tiveram break, porque a partir do momento em que houver um false, a sala não poderia mais ser reservada
                                // se naofoireservadaainda for true, é proque a sala ainda não foi reservada então, pode reservar
                                if(podeReservar) {
                                    inserirSalasreservadasNoBd(campoConfSala.getText().toString(),
                                            nameFuncionario.toString(), campoEvento.getText().toString(), campoDescricao.getText().toString(),
                                            campoDataHoraInicial.getText().toString(), campoDataHoraFinal.getText().toString(),
                                            campoSite.getText().toString());
                                    receptor = true;
                                    break;
                                } else {
                                    Toast.makeText(ReservarSala.this, campoConfSala.getText().toString()
                                            + " já está reservado (a) de " + dinicialreservada + " " + horainicialreservada
                                            + " até " + dfinalreservada + " - " + horafinalreservada, Toast.LENGTH_SHORT).show();
                                    receptor = true;
                                    break;
                                }
                            } else {
                                if(campoConfSala.getText().toString().equals("") || campoConfSala.getText().toString().isEmpty()) {
                                    campoConfSala.setError("Selecione a sala que desseja reservar");
                                    campoConfSala.requestFocus();
                                }

                                if(campoEvento.getText().toString().equals("") || campoEvento.getText().toString().isEmpty()) {
                                    campoEvento.setError("Insira o nome do evento");
                                    campoEvento.requestFocus();
                                }

                                if(campoDataHoraInicial.getText().toString().isEmpty() ||
                                        campoDataHoraInicial.getText().toString().equalsIgnoreCase("DATA/HORA FINAL")
                                || !(campoDataHoraInicial.getText().toString().length() == 16)) {
                                    Toast.makeText(ReservarSala.this, "Insira data e hora inicial.", Toast.LENGTH_SHORT).show();
                                }

                                if(campoDataHoraFinal.getText().toString().isEmpty() ||
                                        campoDataHoraFinal.getText().toString().equalsIgnoreCase("DATA/HORA FINAL")
                                        || !(campoDataHoraFinal.getText().toString().length() == 16)) {
                                    Toast.makeText(ReservarSala.this, "Insira data e hora final.", Toast.LENGTH_SHORT).show();
                                }

                               // Toast.makeText(ReservarSala.this, "Preencher todos os campos!", Toast.LENGTH_SHORT).show();
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

        AtualizarReserva.atualizarReservas();
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

        adapter = new ArrayAdapter<String>
                (this,android.R.layout.simple_list_item_1, salareservada) {
            @Override
            // Sobrescreveno o getView para poder por o texto três
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = (TextView) view.findViewById(android.R.id.text1);

                text1.setText(salareservada.get(position));

                // se estiver com defeito mudar da textview
                if(isComDefeito.get(position).equals("true")) {
                    view.setBackgroundColor(Color.argb(100, 245, 103, 91));
                } else {
                    view.setBackgroundColor(Color.WHITE);
                }
                return view;
            }
        };

        campoConfSala.setAdapter(adapter);

        ref.child("salas").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                campoConfSala.setAdapter(adapter);
                for (DataSnapshot dt : dataSnapshot.getChildren()) {
                    String SR = dt.child("nome").getValue(String.class);
                    salareservada.add(SR);
                    // para verificar se a sala nao esta com defeito
                    isComDefeito.add(dt.child("defeito").getValue(String.class));
                }

                // para visualizar salas em ordem crescente por nome
                Collections.sort(salareservada);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        jaforamreservadas = new ArrayList<>();
        ref.child("salas_reservadas").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {

                    String nomesala = snap.child("Sala").getValue(String.class);
                    String dhinicio = snap.child("Data_Inicial").getValue(String.class);
                    String dhfinal = snap.child("Data_Final").getValue(String.class);

                    Pair<String, String> datahorainiefim = new Pair(dhinicio, dhfinal);

                    Pair<String, Pair<String, String> > salareservadaedata = new Pair(nomesala, datahorainiefim);

                    jaforamreservadas.add(salareservadaedata);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    @Override
    protected void onStop() {
        super.onStop();
        // sempre que parar o app, verificar as salas reservadas e remover as que a data de reserva final tenha passado
        AtualizarReserva.atualizarReservas();
    }

}
