package projetoi.meucarro.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;

import projetoi.meucarro.R;
import projetoi.meucarro.models.Carro;
import projetoi.meucarro.models.User;
import projetoi.meucarro.models.Venda;

public class CriarVendaDialog extends Dialog {

    private ValueEventListener carrosUserListener;
    private Button vendaCarroBtn;
    private User user;
    private EditText valorEdit;
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private ArrayAdapter adapter;
    private Spinner spinner;
    private ArrayList<Carro> userCarrosList;

    public CriarVendaDialog(Activity activity) {
        super(activity);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_criarvenda);


        vendaCarroBtn = (Button) findViewById(R.id.btnVendaCarro);
        valorEdit = (EditText) findViewById(R.id.vendacarro_valor);
        spinner = (Spinner) findViewById(R.id.vendacarro_spinner);
        userCarrosList = new ArrayList<>();


        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        DatabaseReference carrosUserRef = database.getReference().child("users").child(mAuth.getCurrentUser().getUid());

        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, userCarrosList);

        spinner.setAdapter(adapter);

        loadInfo();

        carrosUserRef.addValueEventListener(carrosUserListener);

        vendaCarroBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userCarrosList.isEmpty()) {
                    Toast.makeText(getContext(), R.string.erro_trocarcarro_zero_carros, Toast.LENGTH_SHORT).show();
                }
                else if (user.phone.isEmpty() || user.name.isEmpty() || user.ZIPcode.isEmpty()) {
                    Toast.makeText(getContext(), R.string.erro_vendacarro_completarperfil, Toast.LENGTH_SHORT).show();
                }
                else {
                    confirmarVenda();
                    dismiss();
                }
            }
        });
    }

    private void confirmarVenda() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Carro carroEscolhido = (Carro) spinner.getSelectedItem();

        String carroId = String.valueOf(userCarrosList.indexOf(carroEscolhido));
        double valor = Double.parseDouble(valorEdit.getText().toString());

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("vendas");

        Venda venda = new Venda(userId, "", carroId,  carroEscolhido.modelo, carroEscolhido.ano, valor, false);

        ref.child(userId).child(carroId).setValue(venda);

        Toast.makeText(getContext(), R.string.vendacarro_mensagemsucesso, Toast.LENGTH_SHORT).show();
    }

    private void loadInfo() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Carregando dados...");
        progressDialog.show();


        carrosUserListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);

                if (user.cars == null) {
                    Toast.makeText(getContext(), R.string.criarvendadialog_erronenhumcarro, Toast.LENGTH_SHORT).show();
                    dismiss();
                } else {
                    for (Carro carro : user.cars) {
                        userCarrosList.add(carro);
                        adapter.notifyDataSetChanged();
                    }
                }
                progressDialog.dismiss();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };

    }
}