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
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import projetoi.meucarro.adapters.ComparaCarroAdapter;
import projetoi.meucarro.adapters.StatusRowAdapter;
import projetoi.meucarro.models.Carro;
import projetoi.meucarro.models.User;
import projetoi.meucarro.utils.Comparacao;
import projetoi.meucarro.utils.StatusAdapterPlaceholder;

public class CompararCarroFragment extends Fragment {

    private DatabaseReference userRef;
    private FirebaseAuth mAuth;
    private ArrayList<Carro> userCarrosList;
    private Spinner spinner1;
    private Spinner spinner2;
    private ArrayList<Comparacao> comparacaoArrayList;
    private ArrayAdapter adapter;



    private Button btnCompara;
    private Context act;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_comparar_carro, container, false);

        act = getActivity();

        userCarrosList = new ArrayList<>();
        comparacaoArrayList = new ArrayList<>();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        this.mAuth = FirebaseAuth.getInstance();
        this.userRef = database.getReference().child("users").child(mAuth.getCurrentUser().getUid());

        spinner1 = (Spinner) rootView.findViewById(R.id.compararcarro_spinner1);
        spinner2 = (Spinner) rootView.findViewById(R.id.compararcarro_spinner2);

        ListView listView = (ListView) rootView.findViewById(R.id.listViewCompara);
        adapter = new ComparaCarroAdapter(act, comparacaoArrayList);
        listView.setAdapter(adapter);


        btnCompara = (Button) rootView.findViewById(R.id.buttonCompara);

        final ArrayAdapter<Carro> adapter = new ArrayAdapter<>(act, android.R.layout.simple_spinner_item, userCarrosList);

        spinner1.setAdapter(adapter);
        spinner2.setAdapter(adapter);

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, final long id) {
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
        comparacaoArrayList.clear();
        Carro c1 = (Carro) spinner1.getSelectedItem();
        Carro c2 = (Carro) spinner2.getSelectedItem();

        for (String gasto: createStringGastos()){
            Double gastoc1 = c1.getSomaDeGastoPorTipo(gasto);
            Double gastoc2 = c2.getSomaDeGastoPorTipo(gasto);
            comparacaoArrayList.add(new Comparacao(gasto, gastoc1, gastoc2));
            adapter.notifyDataSetChanged();
        }
    }

    private List<String> createStringGastos() {
        String [] gastosArray = getResources().getStringArray(R.array.adicionardialog_gastosarray);
        return Arrays.asList(gastosArray);
    }

}
