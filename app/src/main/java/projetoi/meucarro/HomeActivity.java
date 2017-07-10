package projetoi.meucarro;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import projetoi.meucarro.adapters.HomeGastosAdapter;
import projetoi.meucarro.dialog.AdicionarGastoDialog;
import projetoi.meucarro.models.CarroUser;
import projetoi.meucarro.models.Gasto;
import projetoi.meucarro.models.GastoCombustivel;
import projetoi.meucarro.utils.CheckStatus;


public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private float autonomia = 0;
    private ListView carrosListView;
    private ArrayList<Gasto> carroGastosList;
    private ArrayAdapter<Gasto> adapter;
    private ValueEventListener carrosUserListener;
    private TextView nomeDoCarroTextView;
    private CarroUser carroUser;
    private FirebaseDatabase database;
    private DatabaseReference carrosUserRef;
    private String lastCarId;
    private FloatingActionButton fab;
    private TextView qteRodagem;
    private HashMap manutencaoHash;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mAuth = FirebaseAuth.getInstance();

        carrosListView = (ListView) findViewById(R.id.homeListView);

        carroGastosList = new ArrayList<>();
        adapter = new HomeGastosAdapter(this, carroGastosList);

        database = FirebaseDatabase.getInstance();
        carrosUserRef = database.getReference();
        carrosListView.setAdapter(adapter);

        updateListView();

        nomeDoCarroTextView = (TextView) findViewById(R.id.home_nome_carro);
        qteRodagem = (TextView) findViewById(R.id.qteKmsRodados);

        carrosUserRef.addValueEventListener(carrosUserListener);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    adicionarGastoDialog();
            }
        });
    }

    private void updateListView() {
        final ProgressDialog progressDialog = new ProgressDialog(HomeActivity.this);
        progressDialog.setMessage("Carregando dados...");
        progressDialog.show();


        carrosUserListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                carroGastosList.clear();
                DataSnapshot carSnapshot = dataSnapshot.child("users").child(mAuth.getCurrentUser().getUid());
                if (carSnapshot.child("lastCar").getValue() != null) {
                    fab.setVisibility(View.VISIBLE);
                    lastCarId = carSnapshot.child("lastCar").getValue().toString();
                    carroUser = carSnapshot.child("carrosList").child(lastCarId).getValue(CarroUser.class);
                    nomeDoCarroTextView.setText(carroUser.modelo);
                    qteRodagem.setText(String.valueOf(carroUser.kmRodados));

                    if (carroUser.listaGastos != null) {
                        for (Gasto gasto : carroUser.listaGastos) {
                            carroGastosList.add(gasto);
                            adapter.notifyDataSetChanged();
                        }
                    }

                    DataSnapshot carrosDaMarca = dataSnapshot.child("carros").child(carroUser.marca);
                    for (DataSnapshot ids : carrosDaMarca.getChildren()) {
                        if (ids.child("Modelo").getValue().toString().equals(carroUser.modelo)) {
                            manutencaoHash = (HashMap) ids.child("Manutenção").getValue();
                        }
                    }


                } else {
                    fab.setVisibility(View.INVISIBLE);
                    Toast.makeText(HomeActivity.this, R.string.msg_home_listacarrosvazia,
                            Toast.LENGTH_LONG).show();
                }
                progressDialog.dismiss();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Nome", "loadPost:onCancelled", databaseError.toException());
            }
        };
    }

    private void adicionarGastoDialog() {
        AdicionarGastoDialog adicionarGastoDialog = new AdicionarGastoDialog(HomeActivity.this);
        adicionarGastoDialog.setInfo(carroUser, lastCarId, manutencaoHash);
        adicionarGastoDialog.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.menu_logout) {
            Intent it = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(it);
            mAuth.signOut();
            finish();
        } else if (id == R.id.menu_adicionar_carro) {
            Intent it = new Intent(HomeActivity.this, AdicionarCarroActivity.class);
            it.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(it);
        } else if (id == R.id.menu_trocar_carro) {
            Intent it = new Intent(HomeActivity.this, TrocarCarroActivity.class);
            it.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(it);
        } else if (id == R.id.menu_calculadora) {
            Intent it = new Intent(HomeActivity.this, GasCalculatorActivity.class);
            it.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(it);
        } else if (id == R.id.expenses_report_menu) {
            Intent it = new Intent(HomeActivity.this, ExpensesReportActivity.class);
            it.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(it);
        } else if (id == R.id.menu_status_carro) {
            Intent it = new Intent(HomeActivity.this, CarroStatusActivity.class);
            it.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(it);
        } else if (id == R.id.menu_oficina) {
            Intent it = new Intent(HomeActivity.this, OficinasActivity.class);
            it.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(it);
        }

        return super.onOptionsItemSelected(item);
    }
}
