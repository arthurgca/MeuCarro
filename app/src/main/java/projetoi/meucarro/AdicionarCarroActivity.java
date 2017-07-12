package projetoi.meucarro;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import projetoi.meucarro.models.Carro;
import projetoi.meucarro.models.Gasto;

public class AdicionarCarroActivity extends AppCompatActivity {

    private Spinner spinnerMarca;
    private Spinner spinnerModelo;
    private Spinner spinnerAno;

    private EditText placa;

    private Button adicionarButton;
    private FirebaseAuth mAuth;
    private ArrayList<Integer> anoCarroList;
    private ArrayAdapter<Integer> adapterAno;
    private HashMap<String, List<Integer>> modeloMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_carro);

        spinnerMarca = (Spinner) findViewById(R.id.adicionarCarroSpinnerMarca);
        spinnerModelo = (Spinner) findViewById(R.id.adicionarCarroSpinnerModelo);
        spinnerAno = (Spinner) findViewById(R.id.adicionarCarroSpinnerAno);

        placa = (EditText) findViewById(R.id.editTextPlaca);

        mAuth = FirebaseAuth.getInstance();

        adicionarButton = (Button) findViewById(R.id.confirmaAdicionarCarro);

        final ArrayList<String> carrosModeloList = new ArrayList<>();
        anoCarroList = new ArrayList<>();
        final ArrayList<String> carrosMarcaList = new ArrayList<>();
        modeloMap = new HashMap<>();

        final ArrayAdapter<String> adapterMarca = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, carrosMarcaList);
        final ArrayAdapter<String> adapterModelo = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, carrosModeloList);
        adapterAno =  new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, anoCarroList);

        adapterMarca.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMarca.setAdapter(adapterMarca);

        adapterModelo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerModelo.setAdapter(adapterModelo);

        spinnerAno.setAdapter(adapterAno);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference carrosRef = database.getReference().child("carros");
        final DatabaseReference usersRef = database.getReference().child("users");


        final ProgressDialog progressDialog = new ProgressDialog(AdicionarCarroActivity.this);
        progressDialog.setMessage("Carregando dados...");
        progressDialog.show();
        progressDialog.setCancelable(false);


        final ValueEventListener spinnerMarcaListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot marcas : dataSnapshot.getChildren()) {
                    carrosMarcaList.add(marcas.getKey());
                    adapterMarca.notifyDataSetChanged();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Nome", "loadPost:onCancelled", databaseError.toException());
            }
        };

        carrosRef.addValueEventListener(spinnerMarcaListener);



        spinnerMarca.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, final long id) {
                modeloMap.clear();
                carrosModeloList.clear();
                DatabaseReference modelosRef = carrosRef.child(carrosMarcaList.get(position));
                final ValueEventListener spinnerModeloListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot ids : dataSnapshot.getChildren()) {
                            String modeloString = ids.child("Modelo").getValue().toString();
                            carrosModeloList.add(modeloString);
                            adapterModelo.notifyDataSetChanged();
                            int anoLancamento = Integer.valueOf(String.valueOf(ids.child("Ano Lançamento").getValue()));
                            int anoFim = Integer.valueOf(String.valueOf(ids.child("Até").getValue()));
                            modeloMap.put(modeloString, buildAnoList(anoLancamento, anoFim));
                            updateAnoSpinner(modeloString);
                            }
                        }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("Nome", "loadPost:onCancelled", databaseError.toException());
                    }
                };
                modelosRef.addValueEventListener(spinnerModeloListener);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerModelo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateAnoSpinner(carrosModeloList.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        adicionarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String marcaSelecionada = carrosMarcaList.get(spinnerMarca.getSelectedItemPosition());
                String modeloCarroSelecionado = carrosModeloList.get(spinnerModelo.getSelectedItemPosition());
                String modeloAnoSelecionado = spinnerAno.getSelectedItem().toString();
                String placaCarro = placa.getText().toString();
                Carro carro = new Carro(marcaSelecionada, modeloCarroSelecionado, modeloAnoSelecionado, placaCarro, 0, new ArrayList<Gasto>());
                String uidLastCar = usersRef.child(mAuth.getCurrentUser().getUid()).child("carrosList").push().getKey();
                usersRef.child(mAuth.getCurrentUser().getUid()).child("carrosList").child(uidLastCar).setValue(carro);
                usersRef.child(mAuth.getCurrentUser().getUid()).child("lastCar").setValue(uidLastCar);
                finish();
            }
        });
    }

    private void updateAnoSpinner(String modelo) {
        anoCarroList.clear();
        anoCarroList.addAll(modeloMap.get(modelo));
        adapterAno.notifyDataSetChanged();
    }


    private List<Integer> buildAnoList(int startDate, int finishDate) {
        List<Integer> anoList = new ArrayList<>();
        for (int i = startDate; i <= finishDate; i++) {
            anoList.add(i);
        }
        return anoList;
    }
}
