package projetoi.meucarro;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import projetoi.meucarro.adapters.StatusRowAdapter;
import projetoi.meucarro.models.CarroUser;
import projetoi.meucarro.utils.CheckStatus;
import projetoi.meucarro.utils.StatusAdapterPlaceholder;

public class CarroStatusActivity extends AppCompatActivity {

    private DatabaseReference carrosUserRef;
    private String lastCarId;
    private CarroUser currentCar;
    private FirebaseAuth mAuth;
    private ArrayAdapter adapter;
    private ArrayList<StatusAdapterPlaceholder> placeHolderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carro_status);

        mAuth = FirebaseAuth.getInstance();
        placeHolderList = new ArrayList<>();

        ListView listView = (ListView) findViewById(R.id.carro_status_listview);
        adapter = new StatusRowAdapter(this, placeHolderList);

        listView.setAdapter(adapter);

        ValueEventListener carrosUserListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot dbUsers = dataSnapshot.child("users").child(mAuth.getCurrentUser().getUid());
                if (dbUsers.child("lastCar").getValue() != null) {
                    lastCarId = dbUsers.child("lastCar").getValue().toString();
                    currentCar = dbUsers.child("carrosList").child(lastCarId).getValue(CarroUser.class);

                    if (currentCar.listaGastos != null) {
                        DataSnapshot carrosDaMarca = dataSnapshot.child("carros").child(currentCar.marca);
                        for (DataSnapshot ids : carrosDaMarca.getChildren()) {
                            if (ids.child("Modelo").getValue().toString().equals(currentCar.modelo)) {
                                placeHolderList.addAll(CheckStatus.checaStatus((HashMap) ids.child("Manutenção").getValue(), currentCar));
                            }
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(CarroStatusActivity.this, R.string.erro_nenhum_gasto,
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(CarroStatusActivity.this, R.string.msg_home_listacarrosvazia,
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Nome", "loadPost:onCancelled", databaseError.toException());
            }
        };

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        this.carrosUserRef = database.getReference();
        this.carrosUserRef.addValueEventListener(carrosUserListener);
    }

}


