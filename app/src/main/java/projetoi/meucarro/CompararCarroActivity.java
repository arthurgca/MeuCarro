package projetoi.meucarro;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import projetoi.meucarro.models.CarroUser;

public class CompararCarroActivity extends AppCompatActivity {

    private ArrayList<CarroUser> carroList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        carroList = new ArrayList<>();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comparar_carro);

        //pega instancia do firebase com referencia a lista de carros do user
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        DatabaseReference carrosUserRef = database.getReference()
                .child("users")
                .child(mAuth.getCurrentUser().getUid())
                .child("carrosList");

        //adiciona o listener na referencia
        carrosUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot carroUser : dataSnapshot.getChildren()) {
                    //aqui tu pode fazer o que quiser com o carro
                    CarroUser carro = carroUser.getValue(CarroUser.class);

                    Log.d("Carro", carro.toString());

                    carroList.add(carro);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
