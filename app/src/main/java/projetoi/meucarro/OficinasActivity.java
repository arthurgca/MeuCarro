package projetoi.meucarro;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import projetoi.meucarro.adapters.OficinaAdapter;
import projetoi.meucarro.adapters.StatusRowAdapter;
import projetoi.meucarro.dialog.AdicionarOficinaDialog;
import projetoi.meucarro.models.CarroUser;
import projetoi.meucarro.models.Gasto;
import projetoi.meucarro.models.GastoCombustivel;
import projetoi.meucarro.models.Oficina;
import projetoi.meucarro.utils.StatusAdapterPlaceholder;

public class OficinasActivity extends AppCompatActivity {

    private DatabaseReference userRef;
    private FirebaseAuth mAuth;

    private FloatingActionButton fab;

    private ValueEventListener oficinaListener;
    private ArrayList<Oficina> oficinasList;
    private String lastCarId;
    private Oficina oficina;
    private DatabaseReference carrosUserRef;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private ArrayList<Oficina> oficinaList;
    private ArrayAdapter<Oficina> adapter;
    private ListView oficinasListView;

    private TextView nomeOficinaTextView;
    private TextView enderecoOficinaTextView;
    private TextView telefoneOficinaTextView;
    private RatingBar notaOficinaRatingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oficinas);

        mAuth = FirebaseAuth.getInstance();

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adicionarOficina();
            }
        });

        oficinasListView = (ListView) findViewById(R.id.oficinasListView);

        oficinaList = new ArrayList<>();
        oficinasList = new ArrayList<>();
        adapter = new OficinaAdapter(this, oficinaList);

        database = FirebaseDatabase.getInstance();
        carrosUserRef = database.getReference().child("users").child(mAuth.getCurrentUser().getUid());
        oficinasListView.setAdapter(adapter);

        updateListView();

        nomeOficinaTextView = (TextView) findViewById(R.id.oficina_nome);
        enderecoOficinaTextView = (TextView) findViewById(R.id.oficina_endereco);
        telefoneOficinaTextView = (TextView) findViewById(R.id.oficina_telefone);
        notaOficinaRatingBar = (RatingBar) findViewById(R.id.oficinaNota);

        carrosUserRef.addValueEventListener(oficinaListener);

    }

    private void adicionarOficina() {
        AdicionarOficinaDialog adicionarGastoDialog = new AdicionarOficinaDialog(OficinasActivity.this);
        adicionarGastoDialog.show();
    }

    private void updateListView() {
        /*final ProgressDialog progressDialog = new ProgressDialog(OficinasActivity.this);
        progressDialog.setMessage("Carregando dados...");
        progressDialog.show();*/


        oficinaListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                oficinaList.clear();
                fab.setVisibility(View.VISIBLE);
                DataSnapshot oficinasDatasnapshop = dataSnapshot.child("oficinaList");
                //if (oficinasDatasnapshop.getValue() == null)
                for (DataSnapshot ofici : oficinasDatasnapshop.getChildren()) {
                    Log.d("ofi", ofici.getValue().toString());
                    Oficina ofic = ofici.getValue(Oficina.class);
                    oficinaList.add(ofic);
                    adapter.notifyDataSetChanged();
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Nome", "loadPost:onCancelled", databaseError.toException());
            }
        };
    }

}


