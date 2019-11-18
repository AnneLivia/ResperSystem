package com.anne.respersystem.ui.salasDisponiveis;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.anne.respersystem.InserirSala;
import com.anne.respersystem.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class SalasDisponiveisFragment extends Fragment {

    private ListView listaDeSalas;
    private Button btInserirSala;

    private ArrayAdapter<String> salasAdapter;
    private ArrayList<String> ids;
    private ArrayList<String> salas;
    private ArrayList<String> capacidade;
    private ArrayList<String> local;
    private ArrayList<String> defeito;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();

    private DatabaseReference ref = database.getReference();

    ArrayAdapter<String> adapter;

    private SalasDisponiveisViewModel salasDisponiveisViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        salasDisponiveisViewModel =
                ViewModelProviders.of(this).get(SalasDisponiveisViewModel.class);

        View root = inflater.inflate(R.layout.fragment_salasdisponiveis, container, false);

        // Obtenho a lista de salas disponiveis
        listaDeSalas = (ListView) root.findViewById(R.id.listadesalas);
        btInserirSala = (Button) root.findViewById(R.id.btInserirSala);

        // Botao para inserir sala nova no sistema
        btInserirSala.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent activityInserir = new Intent(getActivity(), InserirSala.class);
                startActivity(activityInserir);
            }
        });

        // Para mudar o status da sala


        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

        // Inicializando arrays
        salas = new ArrayList<String>();
        ids = new ArrayList<String>();
        capacidade = new ArrayList<String>();
        local = new ArrayList<String>();
        defeito = new ArrayList<String>();

        adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(),
                android.R.layout.simple_list_item_2,
                android.R.id.text1,
                salas) {
            @Override
            // Sobrescreveno o getView para poder por o texto três
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                TextView text2 = (TextView) view.findViewById(android.R.id.text2);

                text1.setText(salas.get(position).toString());
                text2.setText("Local: " + local.get(position).toString() + ", Capacidade: " + capacidade.get(position));


                text1.setTextColor(Color.BLACK);
                text2.setTextColor(Color.BLACK);

                text1.setTextSize(20);
                text2.setTextSize(18);

                // se estiver com defeito mudar da textview
                if(defeito.get(position).equals("true")) {
                    view.setBackgroundColor(Color.argb(100, 245, 103, 91));
                } else {
                    view.setBackgroundColor(Color.WHITE);
                }
                return view;
            }
        };


        listaDeSalas.setAdapter(adapter);


        // populando arrays com os dados
        ref.child("salas").addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                listaDeSalas.setAdapter(adapter);
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    String nomeSala = snap.child("nome").getValue(String.class);
                    String capacidadeSala = snap.child("capacidade").getValue(String.class);
                    String localSala = snap.child("local").getValue(String.class);
                    String id = snap.child("id").getValue(String.class);
                    String isComdefeito = snap.child("defeito").getValue(String.class);
                    salas.add(nomeSala);
                    capacidade.add(capacidadeSala);
                    local.add(localSala);
                    ids.add(id);
                    defeito.add(isComdefeito);
                }

                listaDeSalas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, final View view, final int position, long l) {
                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){
                                    case DialogInterface.BUTTON_POSITIVE:
                                        // coloca para sim
                                        try {
                                            ref.child("salas").child(ids.get(position)).child("defeito").setValue("true");
                                            // ja que o dado nao carrega ao mesmo tempo que o firebase e atualizado, mudar a cor ja aqui
                                            view.setBackgroundColor(Color.argb(100, 245, 103, 91));
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        break;
                                    case DialogInterface.BUTTON_NEGATIVE:
                                        // coloca para nao esta com defeito
                                        try {
                                            ref.child("salas").child(ids.get(position)).child("defeito").setValue("false");
                                            view.setBackgroundColor(Color.WHITE);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        break;
                                }
                            }
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setMessage("A sala está com defeitos ou passando por reformas?")
                                .setPositiveButton("Sim", dialogClickListener)
                                .setNegativeButton("Não", dialogClickListener).show();
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

}
