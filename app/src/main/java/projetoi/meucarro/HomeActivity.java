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
    private ArrayList<Object> userCarros;
    private ArrayAdapter<Object> adapter;
    private ValueEventListener carrosUserListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mAuth = FirebaseAuth.getInstance();

        carrosListView = (ListView) findViewById(R.id.homeListView);

        userCarros = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, userCarros);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference carrosUserRef = database.getReference().child("users").child(mAuth.getCurrentUser().getUid()).child("carrosList");
        carrosListView.setAdapter(adapter);

        updateListView();

        carrosUserRef.addValueEventListener(carrosUserListener);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(HomeActivity.this, AdicionarCarroActivity.class);
                it.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(it);
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
                userCarros.clear();
                for (DataSnapshot carroUser : dataSnapshot.getChildren()) {
                    CarroUser carro = carroUser.getValue(CarroUser.class);
                    userCarros.add(carro);
                    adapter.notifyDataSetChanged();
                    Log.d("Teste", String.valueOf(userCarros.size()));
                }
                progressDialog.hide();
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

        return super.onOptionsItemSelected(item);
    }
}
