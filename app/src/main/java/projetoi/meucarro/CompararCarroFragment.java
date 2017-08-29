package projetoi.meucarro;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import projetoi.meucarro.models.User;

public class CompararCarroFragment extends Fragment {

    private DatabaseReference userRef;
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
    private Context act;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_comparar_carro, container, false);

        act = getActivity();

        userCarrosList = new ArrayList<>();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        this.mAuth = FirebaseAuth.getInstance();
        this.userRef = database.getReference().child("users").child(mAuth.getCurrentUser().getUid());

        spinner1 = (Spinner) rootView.findViewById(R.id.compararcarro_spinner1);
        spinner2 = (Spinner) rootView.findViewById(R.id.compararcarro_spinner2);

        btnCompara = (Button) rootView.findViewById(R.id.buttonCompara);

        textViewSpinner1 = (TextView) rootView.findViewById(R.id.textViewCompara1);
        textViewSpinner2 = (TextView) rootView.findViewById(R.id.textViewCompara2);

        textViewGastosC1Combustivel = (TextView) rootView.findViewById(R.id.textViewC1Combustivel);
        textViewGastosC2Combustivel = (TextView) rootView.findViewById(R.id.textViewC2Combustivel);

        textViewGastosC1Oleo = (TextView) rootView.findViewById(R.id.textViewC1Oleo);
        textViewGastosC2Oleo = (TextView) rootView.findViewById(R.id.textViewC2Oleo);

        textViewGastosC1Revisao = (TextView) rootView.findViewById(R.id.textViewC1Revisao);
        textViewGastosC2Revisao = (TextView) rootView.findViewById(R.id.textViewC2Revisao);

        textViewGastosC1Pneu = (TextView) rootView.findViewById(R.id.textViewC1Pneu);
        textViewGastosC2Pneu = (TextView) rootView.findViewById(R.id.textViewC2Pneu);

        textViewGastosC1ipva = (TextView) rootView.findViewById(R.id.textViewC1ipva);
        textViewGastosC2ipva = (TextView) rootView.findViewById(R.id.textViewC2ipva);

        textViewGastosC1Pecas = (TextView) rootView.findViewById(R.id.textViewC1Pecas);
        textViewGastosC2Pecas = (TextView) rootView.findViewById(R.id.textViewC2Pecas);

        textViewGastosC1Correia = (TextView) rootView.findViewById(R.id.textViewC1Correia);
        textViewGastosC2Correia = (TextView) rootView.findViewById(R.id.textViewC2Correia);

        textViewGastosC1FiltroArC = (TextView) rootView.findViewById(R.id.textViewC1FiltroArc);
        textViewGastosC2FiltroArC = (TextView) rootView.findViewById(R.id.textViewC2FiltroArc);

        textViewGastosC1FiltroAr = (TextView) rootView.findViewById(R.id.textViewC1FiltroAr);
        textViewGastosC2FiltroAr = (TextView) rootView.findViewById(R.id.textViewC2FiltroAr);

        textViewGastosC1Velas = (TextView) rootView.findViewById(R.id.textViewC1Velas);
        textViewGastosC2Velas = (TextView) rootView.findViewById(R.id.textViewC2Velas);

        textViewGastosC1 = (TextView) rootView.findViewById(R.id.textViewGastosc1);
        textViewGastosC2 = (TextView) rootView.findViewById(R.id.textViewGastosc2);

        final ArrayAdapter<Carro> adapter = new ArrayAdapter<>(act, android.R.layout.simple_spinner_item, userCarrosList);

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

        this.userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                for (Carro carro :  user.cars) {
                    //pega o carro e adiciona numa lista
                    userCarrosList.add(carro);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        btnCompara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                compara();
            }
        });

        return rootView;
    }

    public void compara() {
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
