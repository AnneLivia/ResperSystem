package com.anne.respersystem.ui.calendar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.anne.respersystem.AtualizarReserva;
import com.anne.respersystem.EventosPorDia;
import com.anne.respersystem.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CalendarFragment extends Fragment {

    private CalendarViewModel calendarioViewModel;

    private CalendarView calendar;
    private Button btSelecionarData;
    private TextView tvEventosQuantidade;
    private String day;
    private ArrayList<String> datas;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();

    private DatabaseReference ref = database.getReference();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        calendarioViewModel =
                ViewModelProviders.of(this).get(CalendarViewModel.class);
        View root = inflater.inflate(R.layout.fragment_calendar, container, false);

        calendar = (CalendarView) root.findViewById(R.id.calendarevents);
        btSelecionarData = (Button) root.findViewById(R.id.btselectData);
        tvEventosQuantidade = (TextView) root.findViewById(R.id.tvEventosQuantidade);

        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int ano, int mes, int dia) {
                String d = "";
                String m = "";
                String a = "";

                if(dia < 10) {
                    d+="0";
                }
                d += Integer.toString(dia);

                mes++;
                if(mes < 10) {
                    m+="0";
                }
                m+=Integer.toString(mes);

                a = Integer.toString(ano);

                day = d + "/" + m + "/" + a;

                // passando data para funcao para pegar quantos eventos ocorrerão no dia
                // qtdeventos é alterado dentro da funcao
                tvEventosQuantidade.setText("Número de Eventos em " + day + ": " + searchevents(day));
            }
        });

        btSelecionarData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // somente se houver mais de 0 eventos no dia
                if(searchevents(day) > 0) {
                    Intent intent = new Intent(getActivity(), EventosPorDia.class);
                    // enviando para proxima tela esses arrays com esses valores

                    intent.putExtra("data", day);
                    getActivity().startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                } else {
                    Toast.makeText(getContext(), "Sem eventos para o dia!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return root;
    }

    // funcao para obter quantidade de eventos
    int searchevents(final String day) {
        int qtdevento = 0;
        // evento ocorre no dia
        for(String d : datas) {
            if (d.equals(day)) {
                qtdevento++;
            }
            System.out.println(d + " - " + qtdevento);
        }
        return qtdevento;
    }

    @Override
    public void onStart() {

        datas = new ArrayList<>();

        super.onStart();

        // sempre que iniciar o app, verificar as salas reservadas e remover as que a data de reserva final tenha passado
        AtualizarReserva.atualizarReservas();

        // chama referencia para sala reservadas
        ref.child("salas_reservadas").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    String data = "";
                    // obtem data inicial
                    String datainicial = snap.child("Data_Inicial").getValue(String.class);
                    // percorre toda a string para obter somente data
                    for(int i = 0; i < datainicial.length(); i++) {
                        if(datainicial.charAt(i) != ' ') {
                            data+=datainicial.charAt(i);
                        } else {
                            break; // finalizou data, agora é somente hora então break
                        }
                    }
                    datas.add(data);
                }

                // assim que finalizar, colocar no textview para a data atual
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                String currentDate = sdf.format(new Date(calendar.getDate()));
                day = currentDate;

                tvEventosQuantidade.setText("Número de Eventos em " + currentDate + ": " + searchevents(currentDate));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        // sempre que parar o app, verificar as salas reservadas e remover as que a data de reserva final tenha passado
        AtualizarReserva.atualizarReservas();
    }
}