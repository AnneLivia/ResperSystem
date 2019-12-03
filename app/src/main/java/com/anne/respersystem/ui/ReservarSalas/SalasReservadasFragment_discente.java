package com.anne.respersystem.ui.reservarsalas;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.anne.respersystem.AtualizarReserva;
import com.anne.respersystem.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

public class SalasReservadasFragment_discente extends Fragment {

    private ListView salareservadas;
    private ArrayList<String> ids;
    private ArrayList<String> salas_reservadas;
    private ArrayList<String> Funcionario;
    private ArrayList<String> Evento;
    private ArrayList<String> Descricao;
    private ArrayList<String> DataHora_Inicial;
    private ArrayList<String> DataHora_Final;
    private ArrayList<String> Site;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();

    private DatabaseReference ref = database.getReference();

    ArrayAdapter<String> adapter;

    private SalasReservadasViewModel_discente salasReservadasViewModel_discente;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        salasReservadasViewModel_discente =
                ViewModelProviders.of(this).get(SalasReservadasViewModel_discente.class);
        View root = inflater.inflate(R.layout.fragment_salasreservadas_discente, container, false);

        // Lista de salas reservadas
        salareservadas = (ListView) root.findViewById(R.id.salareservadasdiscente);

        return root;
    }
    @Override
    public void onStart(){
        super.onStart();

        // sempre que iniciar o app, verificar as salas reservadas e remover as que a data de reserva final tenha passado
        AtualizarReserva.atualizarReservas();

        // Inicializando arrays
        salas_reservadas = new ArrayList<String>();
        ids = new ArrayList<String>();
        Funcionario = new ArrayList<String>();
        Evento = new ArrayList<String>();
        Descricao = new ArrayList<String>();
        DataHora_Inicial = new ArrayList<String>();
        DataHora_Final = new ArrayList<String>();
        Site = new ArrayList<String>();

        adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(),
                R.layout.listacustomizada,
                R.id.texto1,
                salas_reservadas){

            @Override
            // Sobrescrevendo o getView para poder por o texto três
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = (TextView) view.findViewById(R.id.texto1);
                TextView text2 = (TextView) view.findViewById(R.id.texto2);
                TextView text3 = (TextView) view.findViewById(R.id.texto3);
                TextView text4 = (TextView) view.findViewById(R.id.texto4);
                TextView text5 = (TextView) view.findViewById(R.id.texto5);
                TextView text6 = (TextView) view.findViewById(R.id.texto6);

                text1.setText(salas_reservadas.get(position).toString());
                text2.setText("Por: " + Funcionario.get(position).toString() );
                text3.setText("    " + Evento.get(position).toString());
                text4.setText("    " + Descricao.get(position).toString());
                text5.setText("    " + Site.get(position).toString());
                text6.setText("Início: " + DataHora_Inicial.get(position).toString() + " - Até: "  + DataHora_Final.get(position).toString());

                text1.setTextColor(Color.BLACK);
                text2.setTextColor(Color.BLACK);
                text3.setTextColor(Color.BLACK);
                text4.setTextColor(Color.BLACK);
                text5.setTextColor(Color.BLACK);
                text6.setTextColor(Color.BLACK);

                text1.setTextSize(20);
                text2.setTextSize(15);
                text3.setTextSize(20);
                text4.setTextSize(18);
                text5.setTextSize(15);
                text6.setTextSize(15);

                // Mudando fontfamily dos texts
                text1.setTypeface(ResourcesCompat.getFont(getContext(), R.font.arsenal));
                text2.setTypeface(ResourcesCompat.getFont(getContext(), R.font.assistant));
                text3.setTypeface(ResourcesCompat.getFont(getContext(), R.font.assistant));
                text4.setTypeface(ResourcesCompat.getFont(getContext(), R.font.assistant));
                text5.setTypeface(ResourcesCompat.getFont(getContext(), R.font.assistant));
                text6.setTypeface(ResourcesCompat.getFont(getContext(), R.font.assistant));

                return view;
            }
        };

        salareservadas.setAdapter(adapter);


        // populando arrays com os dados
        ref.child("salas_reservadas").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                salareservadas.setAdapter(adapter);
                for (DataSnapshot snap : dataSnapshot.getChildren()) {

                    String Salareservada = snap.child("Sala").getValue(String.class);
                    String Funcionarioreserva = snap.child("Funcionario").getValue(String.class);
                    String Eventomarcado = snap.child("Evento").getValue(String.class);
                    String DescricaoEvento = snap.child("Descricao").getValue(String.class);
                    String DtHoraInicial = snap.child("Data_Inicial").getValue(String.class);
                    String DtHoraFinal = snap.child("Data_Final").getValue(String.class);
                    String PegaSite = snap.child("Site").getValue(String.class);
                    String id = snap.child("id").getValue(String.class);

                    salas_reservadas.add(Salareservada);
                    Funcionario.add(Funcionarioreserva);
                    Evento.add(Eventomarcado);
                    Descricao.add(DescricaoEvento);
                    DataHora_Inicial.add(DtHoraInicial);
                    DataHora_Final.add(DtHoraFinal);
                    Site.add(PegaSite);
                    ids.add(id);
                }
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