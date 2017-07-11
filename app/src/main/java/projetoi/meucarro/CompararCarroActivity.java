package projetoi.meucarro;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import projetoi.meucarro.models.CarroUser;

public class CompararCarroActivity extends AppCompatActivity {

    private DatabaseReference carrosUserRef;
    private FirebaseAuth mAuth;
    private ArrayList<CarroUser> userCarrosList;
    private Spinner spinner1;
    private Spinner spinner2;

    private Button btnCompara;

    private TextView textViewSpinner1;
    private TextView textViewGastosC1Combustivel;
    private TextView textViewGastosC1Oleo;
    private TextView textViewGastosC1Pneu;
    private TextView textViewGastosC1Pecas;
    private TextView textViewGastosC1Correia;
    private TextView textViewGastosC1FiltroArC;
    private TextView textViewGastosC1FiltroAr;
    private TextView textViewGastosC1Velas;
    private TextView textViewGastosC1Revisao;

    private TextView textViewSpinner2;
    private TextView textViewGastosC2Combustivel;
    private TextView textViewGastosC2Oleo;
    private TextView textViewGastosC2Pneu;
    private TextView textViewGastosC2Pecas;
    private TextView textViewGastosC2Correia;
    private TextView textViewGastosC2FiltroArC;
    private TextView textViewGastosC2FiltroAr;
    private TextView textViewGastosC2Velas;
    private TextView textViewGastosC2Revisao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comparar_carro);

        userCarrosList = new ArrayList<>();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        this.mAuth = FirebaseAuth.getInstance();
        this.carrosUserRef = database.getReference().child("users").child(mAuth.getCurrentUser().getUid());

        spinner1 = (Spinner) findViewById(R.id.compararcarro_spinner1);
        spinner2 = (Spinner) findViewById(R.id.compararcarro_spinner2);

        btnCompara = (Button) findViewById(R.id.buttonCompara);

        textViewSpinner1 = (TextView) findViewById(R.id.textViewCompara1);
        textViewSpinner2 = (TextView) findViewById(R.id.textViewCompara2);

        textViewGastosC1Combustivel = (TextView) findViewById(R.id.textViewC1Combustivel);
        textViewGastosC2Combustivel = (TextView) findViewById(R.id.textViewC2Combustivel);

        textViewGastosC1Oleo = (TextView) findViewById(R.id.textViewC1Oleo);
        textViewGastosC2Oleo = (TextView) findViewById(R.id.textViewC2Oleo);

        textViewGastosC1Revisao = (TextView) findViewById(R.id.textViewC1Revisao);
        textViewGastosC2Revisao = (TextView) findViewById(R.id.textViewC2Revisao);

        final ArrayAdapter<CarroUser> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, userCarrosList);

        spinner1.setAdapter(adapter);
        spinner2.setAdapter(adapter);

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, final long id) {
                textViewSpinner1.setText("Gastos do " + userCarrosList.get(position).getModelo());
                //textViewGastosC1Combustivel.setText(userCarrosList.get(position).getSomaDeGastoPorTipo("Combustível").toString());
                //textViewGastosC1Oleo.setText(userCarrosList.get(position).getSomaDeGastoPorTipo("Troca de Óleo").toString());
                //textViewGastosC1Revisao.setText(userCarrosList.get(position).getSomaDeGastoPorTipo("Revisão").toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, final long id) {
                textViewSpinner2.setText("Gastos do " + userCarrosList.get(position).getModelo());
                //textViewGastosC2Combustivel.setText(userCarrosList.get(position).getSomaDeGastoPorTipo("Combustível").toString());
                //textViewGastosC2Oleo.setText(userCarrosList.get(position).getSomaDeGastoPorTipo("Troca de Óleo").toString());
                //textViewGastosC2Revisao.setText(userCarrosList.get(position).getSomaDeGastoPorTipo("Revisão").toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        this.carrosUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dsCarro :  dataSnapshot.child("carrosList").getChildren()) {
                    //pega o carro e adiciona numa lista
                    CarroUser carro = dsCarro.getValue(CarroUser.class);
                    userCarrosList.add(carro);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    public void compara(View view) {
        CarroUser c1 = (CarroUser) spinner1.getSelectedItem();
        CarroUser c2 = (CarroUser) spinner2.getSelectedItem();

        textViewGastosC1Combustivel.setText(c1.getSomaDeGastoPorTipo("Combustível").toString());
        textViewGastosC1Oleo.setText(c1.getSomaDeGastoPorTipo("Troca de Óleo").toString());
        textViewGastosC1Revisao.setText(c1.getSomaDeGastoPorTipo("Revisão").toString());

        textViewGastosC2Combustivel.setText(c2.getSomaDeGastoPorTipo("Combustível").toString());
        textViewGastosC2Oleo.setText(c2.getSomaDeGastoPorTipo("Troca de Óleo").toString());
        textViewGastosC2Revisao.setText(c2.getSomaDeGastoPorTipo("Revisão").toString());

        Toast.makeText(this, "Carro1: " + c1.toString() + " e " + "Carro2: " + c2.toString(), Toast.LENGTH_SHORT).show();
    }

}
