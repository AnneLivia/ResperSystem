package com.anne.respersystem;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.annotation.NonNull;

public class AtualizarReserva {

    final static FirebaseDatabase database = FirebaseDatabase.getInstance();

    private static DatabaseReference ref = database.getReference();

    static public void atualizarReservas() {
        // populando arrays com os dados
        ref.child("salas_reservadas").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    String datahora_final = snap.child("Data_Final").getValue(String.class);

                    // obtendo data e hora separado
                    String data = "", hora = "";
                    boolean hour = false; // para informar o momento de pegar somente a hora

                    for (int i = 0; i < datahora_final.length(); i++) {
                        if (datahora_final.charAt(i) != ' ' && !hour) {
                            data += datahora_final.charAt(i);
                        } else {
                            if (datahora_final.charAt(i) != ' ')
                                hora += datahora_final.charAt(i);
                            hour = true;
                        }
                    }

                    SimpleDateFormat sdfdata = new SimpleDateFormat("dd/MM/yyyy");
                    SimpleDateFormat sdfhora = new SimpleDateFormat("HH:mm");

                    // transformando data reservada para Date para poder fazer as comparações depois
                    Date datafinalreservada = new Date();
                    try {
                        datafinalreservada = sdfdata.parse(data);
                    } catch (ParseException pex) {
                        pex.printStackTrace();
                    }

                    // obtendo horário atual
                    Date dataatual = new Date();
                    String horaatual = sdfhora.format(dataatual);
                    String diaatual = sdfdata.format(dataatual);

                    // transformando data atual para Date para poder fazer as comparações depois
                    Date diafinalatual = new Date();
                    try {
                        diafinalatual = sdfdata.parse(diaatual);
                    } catch (ParseException pex) {
                        pex.printStackTrace();
                    }

                    // verificando se o tempo do evento expirou
                    if (datafinalreservada.compareTo(diafinalatual) < 0) {
                        // se entrou aqui, a data final do evento já passou do dia atual, então remover
                        ref.child("salas_reservadas").child(snap.child("id").getValue(String.class)).removeValue();
                    } else if (datafinalreservada.compareTo(diafinalatual) == 0) {
                        // se entrou aqui, data atual é igual a do final do evento, logo verificar hora
                        if (hora.compareTo(horaatual) <= 0) {
                            // se entrou aqui, hora atual já é maior ou igual a hora final reservada, então remover a reserva do sistema
                            ref.child("salas_reservadas").child(snap.child("id").getValue(String.class)).removeValue();
                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
