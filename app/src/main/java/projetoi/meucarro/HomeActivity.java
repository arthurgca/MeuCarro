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

import projetoi.meucarro.models.CarroUser;


public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private ListView carrosListView;
    private ArrayList<CarroUser> carroGastosList;
    private ArrayAdapter<CarroUser> adapter;
    private ValueEventListener carrosUserListener;
    private TextView nomeDoCarroTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mAuth = FirebaseAuth.getInstance();

        carrosListView = (ListView) findViewById(R.id.homeListView);

        carroGastosList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, carroGastosList);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference carrosUserRef = database.getReference().child("users").child(mAuth.getCurrentUser().getUid());
        carrosListView.setAdapter(adapter);

        updateListView();

        nomeDoCarroTextView = (TextView) findViewById(R.id.home_nome_carro);

        carrosUserRef.addValueEventListener(carrosUserListener);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
                String lastCarId;
                if (dataSnapshot.child("lastCar").getValue() != null) {
                    lastCarId = dataSnapshot.child("lastCar").getValue().toString();
                    CarroUser carroUser = dataSnapshot.child("carrosList").child(lastCarId).getValue(CarroUser.class);
                        nomeDoCarroTextView.setText(carroUser.modelo);
                } else {
                    Toast.makeText(HomeActivity.this, R.string.home_erro_nenhum_carro,
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
        }

        if (id == R.id.menu_adicionar_carro) {
            Intent it = new Intent(HomeActivity.this, AdicionarCarroActivity.class);
            it.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(it);
        }

        if (id == R.id.menu_trocar_carro) {
            Intent it = new Intent(HomeActivity.this, TrocarCarroActivity.class);
            it.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(it);
        }

        if (id == R.id.menu_calculadora) {
            Intent it = new Intent(HomeActivity.this, GasCalculatorActivity.class);
            it.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(it);
        }

        return super.onOptionsItemSelected(item);
    }
}
