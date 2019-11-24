package com.anne.respersystem.ui.reservarsalas;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.anne.respersystem.R;
import com.anne.respersystem.ReservarSala;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

public class ReservarSalasFragment extends Fragment {

    private ListView salareservadas;
    private Button btReservar;

    private ArrayAdapter<String> salasAdapter;
    private ArrayList<String> ids;
    private ArrayList<String> userUid;
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


    private ReservarSalasViewModel reservarSalasViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        reservarSalasViewModel =
                ViewModelProviders.of(this).get(ReservarSalasViewModel.class);
        View root = inflater.inflate(R.layout.fragment_reservarsala, container, false);

        // Lista de salas reservadas
        salareservadas = (ListView) root.findViewById(R.id.salareservadas);
        btReservar = (Button) root.findViewById(R.id.btReservar);


        btReservar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent activityReserva = new Intent(getActivity(), ReservarSala.class);
                startActivity(activityReserva);

                // animacao ao mudar de acitivty
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        return root;
    }

    @Override
    public void onStart(){
        super.onStart();

        // obtendo nome
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // Inicializando arrays
        salas_reservadas = new ArrayList<String>();
        ids = new ArrayList<String>();
        Funcionario = new ArrayList<String>();
        Evento = new ArrayList<String>();
        Descricao = new ArrayList<String>();
        DataHora_Inicial = new ArrayList<String>();
        DataHora_Final = new ArrayList<String>();
        Site = new ArrayList<String>();
        userUid = new ArrayList<String>();

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
                text3.setText("Evento: " + Evento.get(position).toString());
                text4.setText("Descrição: " + Descricao.get(position).toString());
                text5.setText("Site: " + Site.get(position).toString());
                text6.setText("Início: " + DataHora_Inicial.get(position).toString() + " - Até: "  + DataHora_Final.get(position).toString());

                text1.setTextColor(Color.BLACK);
                text2.setTextColor(Color.BLACK);
                text3.setTextColor(Color.BLACK);
                text4.setTextColor(Color.BLACK);
                text5.setTextColor(Color.BLUE);
                text6.setTextColor(Color.BLACK);

                text1.setTextSize(20);
                text2.setTextSize(15);
                text3.setTextSize(20);
                text4.setTextSize(20);
                text5.setTextSize(15);
                text6.setTextSize(15);

                // Mudando fontfamily dos texts
                text1.setTypeface(ResourcesCompat.getFont(getContext(),R.font.arsenal));
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
                    String userid = snap.child("uid").getValue(String.class);

                    salas_reservadas.add(Salareservada);
                    Funcionario.add(Funcionarioreserva);
                    Evento.add(Eventomarcado);
                    Descricao.add(DescricaoEvento);
                    DataHora_Inicial.add(DtHoraInicial);
                    DataHora_Final.add(DtHoraFinal);
                    Site.add(PegaSite);
                    ids.add(id);
                    // necessario pegar uid do usuario para permitir que ele libere uma sala ou nao
                    // somente pelo nome pode dar problema
                    userUid.add(userid);
                }

                // para "desreservar" a sala do sistema
                salareservadas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(final AdapterView<?> adapterView, final View view, final int position, long l) {
                        // somente se a sala reservada tiver o mesmo id do usuario que esta logado é que ela poderá ser removida
                        if(userUid.get(position).equals(user.getUid())) {
                            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case DialogInterface.BUTTON_POSITIVE:
                                            // coloca para sim
                                            try {
                                                // String defeito = ref.child("salas").child(ids.get(position)).child("defeito").toString();
                                                ref.child("salas_reservadas").child(ids.get(position)).removeValue();
                                                // ja que o dado nao carrega ao mesmo tempo que o firebase e atualizado, remover do firebase logo
                                                adapter.remove(adapter.getItem(position));
                                                Intent intent = getActivity().getIntent();
                                                getActivity().finish();
                                                startActivity(intent);
                                                //getActivity().finish();
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
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.DialogThemeOne);
                            builder.setMessage("Deseja liberar sala?")
                                    .setPositiveButton("Sim", dialogClickListener)
                                    .setNegativeButton("Não", dialogClickListener).show();
                        } else {
                            Toast.makeText(getContext(), "Não foi você que reservou a sala!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



}