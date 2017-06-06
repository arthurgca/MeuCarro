package projetoi.meucarro;

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

import projetoi.meucarro.models.Carro;
import projetoi.meucarro.models.CarroUser;

public class AdicionarCarroActivity extends AppCompatActivity {

    private Spinner spinner;
    private TextView nomeText;
    private TextView modeloText;
    private TextView motorText;
    private TextView anoFabricacaoText;
    private TextView anoModeloText;
    private TextView combustivelText;
    private Button adicionarButton;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_carro);

        spinner = (Spinner) findViewById(R.id.adicionarCarroSpinner);
        mAuth = FirebaseAuth.getInstance();



        adicionarButton = (Button) findViewById(R.id.confirmaAdicionarCarro);

        nomeText = (TextView) findViewById(R.id.adicionarCarroNome);
        modeloText = (TextView) findViewById(R.id.adicionarCarroModelo);
        motorText = (TextView) findViewById(R.id.adicionarCarroMotor);
        anoFabricacaoText = (TextView) findViewById(R.id.adicionarCarroAnoFabricacao);
        anoModeloText = (TextView) findViewById(R.id.adicionarCarroAnoModelo);
        combustivelText = (TextView) findViewById(R.id.adicionarCarroCombustivel);



        final ArrayList<String> carrosNomeList = new ArrayList<>();
        final ArrayList<Carro> carrosList = new ArrayList<>();
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, carrosNomeList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference carrosRef = database.getReference().child("carros");
        final DatabaseReference usersRef = database.getReference().child("users");


        final ValueEventListener spinnerListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot carro : dataSnapshot.getChildren()) {
                    Carro carro1 = carro.getValue(Carro.class);
                    carrosList.add(carro1);
                    carrosNomeList.add(carro1.nome);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Nome", "loadPost:onCancelled", databaseError.toException());
            }
        };

        carrosRef.addValueEventListener(spinnerListener);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                nomeText.setText(carrosList.get(position).nome);
                modeloText.setText(carrosList.get(position).modelo);
                motorText.setText(carrosList.get(position).motor);
                anoFabricacaoText.setText(carrosList.get(position).anoFabricacao);
                anoModeloText.setText(carrosList.get(position).anoModelo);
                combustivelText.setText(carrosList.get(position).combustivel);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        adicionarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CarroUser carroUser = new CarroUser(carrosList.get(spinner.getSelectedItemPosition()), 0);
                usersRef.child(mAuth.getCurrentUser().getUid()).child("carrosList").push().setValue(carroUser);
                finish();
            }
        });
    }
}
