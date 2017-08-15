package projetoi.meucarro;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import projetoi.meucarro.adapters.OficinaAdapter;
import projetoi.meucarro.dialog.AdicionarOficinaDialog;
import projetoi.meucarro.models.Oficina;
import projetoi.meucarro.models.User;

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
    private User user;


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
        AdicionarOficinaDialog adicionarOficinaDialog = new AdicionarOficinaDialog(OficinasActivity.this);
        adicionarOficinaDialog.setInfo(user);
        adicionarOficinaDialog.show();
    }

    private void updateListView() {
        /*final ProgressDialog progressDialog = new ProgressDialog(OficinasActivity.this);
        progressDialog.setMessage("Carregando dados...");
        progressDialog.show();*/


        oficinaListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                oficinaList.clear();
                fab.setVisibility(View.VISIBLE);
                if (user.repairShops != null) {
                    for (Oficina ofici : user.repairShops) {
                        oficinaList.add(ofici);
                        adapter.notifyDataSetChanged();
                    }
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Nome", "loadPost:onCancelled", databaseError.toException());
            }
        };
    }

}


