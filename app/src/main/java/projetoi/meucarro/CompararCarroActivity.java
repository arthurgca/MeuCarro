package projetoi.meucarro;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class CompararCarroActivity extends AppCompatActivity {

    private DatabaseReference carrosUserRef;
    private FirebaseAuth mAuth;
    private ArrayList<Carro> userCarrosList;
    private Spinner spinner1;
    private Spinner spinner2;

    private Button btnCompara;

    private TextView textViewSpinner1;
    private TextView textViewGastosC1Combustivel;
    private TextView textViewGastosC1Oleo;
    private TextView textViewGastosC1Pneu;
    private TextView textViewGastosC1ipva;
    private TextView textViewGastosC1Pecas;
    private TextView textViewGastosC1Correia;
    private TextView textViewGastosC1FiltroArC;
    private TextView textViewGastosC1FiltroAr;
    private TextView textViewGastosC1Velas;
    private TextView textViewGastosC1Revisao;
    private TextView textViewGastosC1;

    private TextView textViewSpinner2;
    private TextView textViewGastosC2Combustivel;
    private TextView textViewGastosC2Oleo;
    private TextView textViewGastosC2Pneu;
    private TextView textViewGastosC2ipva;
    private TextView textViewGastosC2Pecas;
    private TextView textViewGastosC2Correia;
    private TextView textViewGastosC2FiltroArC;
    private TextView textViewGastosC2FiltroAr;
    private TextView textViewGastosC2Velas;
    private TextView textViewGastosC2Revisao;
    private TextView textViewGastosC2;

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

        textViewGastosC1Pneu = (TextView) findViewById(R.id.textViewC1Pneu);
        textViewGastosC2Pneu = (TextView) findViewById(R.id.textViewC2Pneu);

        textViewGastosC1ipva = (TextView) findViewById(R.id.textViewC1ipva);
        textViewGastosC2ipva = (TextView) findViewById(R.id.textViewC2ipva);

        textViewGastosC1Pecas = (TextView) findViewById(R.id.textViewC1Pecas);
        textViewGastosC2Pecas = (TextView) findViewById(R.id.textViewC2Pecas);

        textViewGastosC1Correia = (TextView) findViewById(R.id.textViewC1Correia);
        textViewGastosC2Correia = (TextView) findViewById(R.id.textViewC2Correia);

        textViewGastosC1FiltroArC = (TextView) findViewById(R.id.textViewC1FiltroArc);
        textViewGastosC2FiltroArC = (TextView) findViewById(R.id.textViewC2FiltroArc);

        textViewGastosC1FiltroAr = (TextView) findViewById(R.id.textViewC1FiltroAr);
        textViewGastosC2FiltroAr = (TextView) findViewById(R.id.textViewC2FiltroAr);

        textViewGastosC1Velas = (TextView) findViewById(R.id.textViewC1Velas);
        textViewGastosC2Velas = (TextView) findViewById(R.id.textViewC2Velas);

        textViewGastosC1 = (TextView) findViewById(R.id.textViewGastosc1);
        textViewGastosC2 = (TextView) findViewById(R.id.textViewGastosc2);

        final ArrayAdapter<Carro> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, userCarrosList);

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
                    Carro carro = dsCarro.getValue(Carro.class);
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
        Carro c1 = (Carro) spinner1.getSelectedItem();
        Carro c2 = (Carro) spinner2.getSelectedItem();

        textViewGastosC1Combustivel.setText(c1.getSomaDeGastoPorTipo("Combustível").toString());
        textViewGastosC1Oleo.setText(c1.getSomaDeGastoPorTipo("Troca de Óleo").toString());
        textViewGastosC1Revisao.setText(c1.getSomaDeGastoPorTipo("Revisão").toString());
        textViewGastosC1Pneu.setText(c1.getSomaDeGastoPorTipo("Troca de Pneu").toString());
        textViewGastosC1ipva.setText(c1.getSomaDeGastoPorTipo("IPVA").toString());
        textViewGastosC1Pecas.setText(c1.getSomaDeGastoPorTipo("Compra de Peças").toString());
        textViewGastosC1Correia.setText(c1.getSomaDeGastoPorTipo("Correia Dentada").toString());
        textViewGastosC1FiltroArC.setText(c1.getSomaDeGastoPorTipo("Filtro Ar Condicionado").toString());
        textViewGastosC1FiltroAr.setText(c1.getSomaDeGastoPorTipo("Filtro de Ar").toString());
        textViewGastosC1Velas.setText(c1.getSomaDeGastoPorTipo("Velas").toString());
        textViewGastosC1.setText(c1.getSomaDeGastos().toString());

        textViewGastosC2Combustivel.setText(c2.getSomaDeGastoPorTipo("Combustível").toString());
        textViewGastosC2Oleo.setText(c2.getSomaDeGastoPorTipo("Troca de Óleo").toString());
        textViewGastosC2Revisao.setText(c2.getSomaDeGastoPorTipo("Revisão").toString());
        textViewGastosC2Pneu.setText(c2.getSomaDeGastoPorTipo("Troca de Pneu").toString());
        textViewGastosC2ipva.setText(c2.getSomaDeGastoPorTipo("IPVA").toString());
        textViewGastosC2Pecas.setText(c2.getSomaDeGastoPorTipo("Compra de Peças").toString());
        textViewGastosC2Correia.setText(c2.getSomaDeGastoPorTipo("Correia Dentada").toString());
        textViewGastosC2FiltroArC.setText(c2.getSomaDeGastoPorTipo("Filtro Ar Condicionado").toString());
        textViewGastosC2FiltroAr.setText(c2.getSomaDeGastoPorTipo("Filtro de Ar").toString());
        textViewGastosC2Velas.setText(c2.getSomaDeGastoPorTipo("Velas").toString());
        textViewGastosC2.setText(c2.getSomaDeGastos().toString());

        //Toast.makeText(this, "Carro1: " + c1.toString() + " e " + "Carro2: " + c2.toString(), Toast.LENGTH_SHORT).show();
    }

}
