package projetoi.meucarro;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

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

        final ArrayAdapter<CarroUser> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, userCarrosList);

        spinner1.setAdapter(adapter);
        spinner2.setAdapter(adapter);

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
}
