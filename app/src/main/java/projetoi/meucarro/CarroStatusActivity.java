package projetoi.meucarro;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import projetoi.meucarro.adapters.StatusRowAdapter;
import projetoi.meucarro.models.CarroUser;
import projetoi.meucarro.models.Gasto;
import projetoi.meucarro.utils.StatusAdapterPlaceholder;

public class CarroStatusActivity extends AppCompatActivity {

    private DatabaseReference carrosUserRef;
    private String lastCarId;
    private CarroUser currentCar;
    private FirebaseAuth mAuth;
    private ArrayAdapter adapter;
    private ArrayList<StatusAdapterPlaceholder> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carro_status);

        mAuth = FirebaseAuth.getInstance();
        list = new ArrayList<>();

        ListView listView = (ListView) findViewById(R.id.carro_status_listview);
        adapter = new StatusRowAdapter(this, list);

        listView.setAdapter(adapter);

        ValueEventListener carrosUserListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot dbUsers = dataSnapshot.child("users").child(mAuth.getCurrentUser().getUid());
                if (dbUsers.child("lastCar").getValue() != null) {
                    lastCarId = dbUsers.child("lastCar").getValue().toString();
                    currentCar = dbUsers.child("carrosList").child(lastCarId).getValue(CarroUser.class);

                    if (currentCar.listaGastos != null) {
                        DataSnapshot carrosDaMarca = dataSnapshot.child("carros").child(currentCar.marca);
                        for (DataSnapshot ids : carrosDaMarca.getChildren()) {
                            if (ids.child("Modelo").getValue().toString().equals(currentCar.modelo)) {
                                checaStatus((HashMap) ids.child("Manutenção").getValue());
                            }
                        }
                    } else {
                        Toast.makeText(CarroStatusActivity.this, R.string.erro_nenhum_gasto,
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(CarroStatusActivity.this, R.string.msg_home_listacarrosvazia,
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Nome", "loadPost:onCancelled", databaseError.toException());
            }
        };

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        this.carrosUserRef = database.getReference();
        this.carrosUserRef.addValueEventListener(carrosUserListener);
    }

    private void checaStatus(HashMap manutencao) {
        for (Object i : manutencao.keySet()) {
            StatusAdapterPlaceholder placeholder;
            String mensagem = "";

            Gasto ultimoGasto = null;
            int quantidadeTrocas = 0;

            long valorKm = (long) ((HashMap) manutencao.get(i)).get("Kilometragem");

            for (Gasto gasto : currentCar.listaGastos) {
                if (gasto.descricao.equals(i.toString())) {
                    ultimoGasto = gasto;
                    quantidadeTrocas++;
                }
            }

            boolean atrasado = false;
            String dataManutencao = (String) ((HashMap) manutencao.get(i)).get("Tempo");

            if (ultimoGasto != null) {
                long diferenca = (valorKm - (currentCar.kmRodados - ultimoGasto.registroKm));

                if (dataManutencao != null)
                    if (checkAtrasoData(ultimoGasto.data, dataManutencao)) {
                        atrasado = true;
                        mensagem = mensagem.concat("Atraso por data \n");
                        Log.d("mensagem", mensagem);
                    }

                if (diferenca <= 0) {
                    mensagem = mensagem.concat("Manutenção deveria ter sido efetuada" + "\n" +
                            "Já se passaram " + -diferenca + " km's.");
                    placeholder = new StatusAdapterPlaceholder(i.toString(), mensagem, true);
                    list.add(placeholder);
                } else {
                    mensagem = mensagem.concat("Quantidade efetuada: " + quantidadeTrocas + "\n" +
                            "Faltam: " + diferenca + " km's.");
                    placeholder = new StatusAdapterPlaceholder(i.toString(), mensagem, atrasado);
                    list.add(placeholder);
                }
            } else {
                long diferenca = (valorKm - currentCar.kmRodados);
                if (diferenca <= 0) {
                    mensagem = mensagem.concat("Manutenção deveria ter sido efetuada" + "\n" +
                            "Já se passaram " + -diferenca + " km's.");
                    placeholder = new StatusAdapterPlaceholder(i.toString(), mensagem, true);
                    list.add(placeholder);
                } else {
                    mensagem = mensagem.concat("Faltam: " + diferenca + " km's.");
                    placeholder = new StatusAdapterPlaceholder(i.toString(), mensagem, false);
                    list.add(placeholder);
                }
            }

        }
        adapter.notifyDataSetChanged();
    }

    private boolean checkAtrasoData(Date data, String dataString) {
        Calendar dataAtual = Calendar.getInstance();
        Calendar dataManutencao = Calendar.getInstance();
        dataManutencao.setTime(data);

        if (dataString.equals("3 anos")) {
            dataManutencao.add(Calendar.YEAR, 3);
        } else if (dataString.equals("1 ano")) {
            dataManutencao.add(Calendar.YEAR, 1);
        } else if (dataString.equals("6 meses")) {
            dataManutencao.add(Calendar.MONTH, 6);
        }

        if (dataAtual.compareTo(dataManutencao) > 0) {
            Log.d("dataAtual", dataAtual.toString());
            Log.d("dataManutencao", dataManutencao.toString());
            return true;
        } else
            return false;
        }
    }


