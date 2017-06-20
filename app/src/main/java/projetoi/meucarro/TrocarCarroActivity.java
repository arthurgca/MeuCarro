package projetoi.meucarro;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import projetoi.meucarro.models.CarroUser;

public class TrocarCarroActivity extends AppCompatActivity {

    private ValueEventListener carrosUserListener;
    private RadioGroup rg;
    private ArrayList<CarroUser> userCarros;
    private Button trocarCarroBtn;
    private ArrayList<String> idsList;
    private int qtdeCarros;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trocar_carro);

        qtdeCarros = 0;
        idsList = new ArrayList<>();

        trocarCarroBtn = (Button) findViewById(R.id.buttonTrocarCarro);


        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        DatabaseReference carrosUserRef = database.getReference().child("users").child(mAuth.getCurrentUser().getUid()).child("carrosList");

        updateRadioButtons();

        carrosUserRef.addValueEventListener(carrosUserListener);

        trocarCarroBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rg.getChildCount() <= 0) {
                    Toast.makeText(TrocarCarroActivity.this, R.string.erro_trocarcarro_zero_carros, Toast.LENGTH_SHORT).show();
                }

                else if (rg.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(TrocarCarroActivity.this, R.string.erro_trocarcarro_nenhumaselecao, Toast.LENGTH_SHORT).show();
                }

                else {
                    DatabaseReference usersRef = database.getReference().child("users");
                    String idSelecionado = idsList.get(rg.getCheckedRadioButtonId());
                    usersRef.child(mAuth.getCurrentUser().getUid()).child("lastCar").setValue(idSelecionado);
                    finish();
                }
            }
        });
    }

    private void updateRadioButtons() {
        final ProgressDialog progressDialog = new ProgressDialog(TrocarCarroActivity.this);
        progressDialog.setMessage("Carregando dados...");
        progressDialog.show();

        rg = (RadioGroup) findViewById(R.id.trocar_carro_rg);

        carrosUserListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot carroUser : dataSnapshot.getChildren()) {
                    CarroUser carro = carroUser.getValue(CarroUser.class);
                    RadioButton rbn = new RadioButton(TrocarCarroActivity.this);

                    rbn.setId(qtdeCarros);
                    rbn.setText(carro.modelo);

                    idsList.add(carroUser.getKey());

                    rg.addView(rbn);

                    qtdeCarros++;
                }
                progressDialog.dismiss();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };

    }
}
