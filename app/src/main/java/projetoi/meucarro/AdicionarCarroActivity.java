package projetoi.meucarro;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


import projetoi.meucarro.models.CarroUser;
import projetoi.meucarro.models.Gasto;

public class AdicionarCarroActivity extends AppCompatActivity {

    private Spinner spinnerMarca;
    private Spinner spinnerModelo;

    private TextView marcaText;
    private TextView modeloText;
    private Button adicionarButton;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_carro);

        spinnerMarca = (Spinner) findViewById(R.id.adicionarCarroSpinnerMarca);
        spinnerModelo = (Spinner) findViewById(R.id.adicionarCarroSpinnerModelo);

        mAuth = FirebaseAuth.getInstance();

        adicionarButton = (Button) findViewById(R.id.confirmaAdicionarCarro);

        marcaText = (TextView) findViewById(R.id.adicionarCarroMarca);
        modeloText = (TextView) findViewById(R.id.adicionarCarroModelo);

        final ArrayList<String> carrosModeloList = new ArrayList<>();
        final ArrayList<String> carrosMarcaList = new ArrayList<>();

        final ArrayAdapter<String> adapterMarca = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, carrosMarcaList);
        final ArrayAdapter<String> adapterModelo = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, carrosModeloList);


        adapterMarca.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMarca.setAdapter(adapterMarca);

        adapterModelo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerModelo.setAdapter(adapterModelo);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference carrosRef = database.getReference().child("carros");
        final DatabaseReference usersRef = database.getReference().child("users");


        final ProgressDialog progressDialog = new ProgressDialog(AdicionarCarroActivity.this);
        progressDialog.setMessage("Carregando dados...");
        progressDialog.show();

        final ValueEventListener spinnerMarcaListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot marcas : dataSnapshot.getChildren()) {
                    carrosMarcaList.add(marcas.getKey());
                    adapterMarca.notifyDataSetChanged();
                }
                progressDialog.hide();
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
                carrosModeloList.clear();
                DatabaseReference modelosRef = carrosRef.child(carrosMarcaList.get(position));
                final ValueEventListener spinnerModeloListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot ids : dataSnapshot.getChildren()) {
                            for (DataSnapshot modelo : ids.getChildren()) {
                                carrosModeloList.add(modelo.getValue().toString());
                                adapterModelo.notifyDataSetChanged();
                            }
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



        adicionarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String modeloCarroSelecionado = carrosModeloList.get(spinnerModelo.getSelectedItemPosition());
                CarroUser carroUser = new CarroUser(modeloCarroSelecionado, 0, new ArrayList<Gasto>());
                usersRef.child(mAuth.getCurrentUser().getUid()).child("carrosList").push().setValue(carroUser);
                finish();
            }
        });
    }
}
