package projetoi.meucarro.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import projetoi.meucarro.R;
import projetoi.meucarro.models.Carro;
import projetoi.meucarro.models.User;
import projetoi.meucarro.models.Venda;

public class CriarVendaDialog extends Dialog {

    private ValueEventListener carrosUserListener;
    private RadioGroup rg;
    private Button vendaCarroBtn;
    private ArrayList<Integer> idsList;
    private int qtdeCarros;
    private User user;
    private EditText senhaConfirmacaoEdit;
    private EditText valorEdit;

    public CriarVendaDialog(Activity activity) {
        super(activity);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.criarvenda_dialog);

        qtdeCarros = 0;
        idsList = new ArrayList<>();

        vendaCarroBtn = (Button) findViewById(R.id.btnVendaCarro);
        senhaConfirmacaoEdit = (EditText) findViewById(R.id.vendacarro_editsenha);
        valorEdit = (EditText) findViewById(R.id.vendacarro_valor);


        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        DatabaseReference carrosUserRef = database.getReference().child("users").child(mAuth.getCurrentUser().getUid());

        updateRadioButtons();

        carrosUserRef.addValueEventListener(carrosUserListener);

        vendaCarroBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rg.getChildCount() <= 0) {
                    Toast.makeText(getContext(), R.string.erro_trocarcarro_zero_carros, Toast.LENGTH_SHORT).show();
                }

                else if (rg.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(getContext(), R.string.erro_trocarcarro_nenhumaselecao, Toast.LENGTH_SHORT).show();
                }
                else if (senhaConfirmacaoEdit.getText().toString().length() < 5) {
                    Toast.makeText(getContext(), R.string.criarvendadialog_errosenhapequena, Toast.LENGTH_SHORT).show();
                }
                else if (senhaConfirmacaoEdit.getText().toString().isEmpty() ||
                        valorEdit.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), R.string.erro_vendacarro_valorvazio, Toast.LENGTH_SHORT).show();
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
        String carroId = String.valueOf(rg.getCheckedRadioButtonId());
        String senha = senhaConfirmacaoEdit.getText().toString();
        double valor = Double.parseDouble(valorEdit.getText().toString());
        String carroNome = user.cars.get(rg.getCheckedRadioButtonId()).modelo;
        String carroAno = user.cars.get(rg.getCheckedRadioButtonId()).ano;

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("vendas");

        Venda venda = new Venda(userId, "", carroId,  carroNome, carroAno, senha, valor, false);

        ref.child(userId).child(String.valueOf(rg.getCheckedRadioButtonId())).setValue(venda);

        Toast.makeText(getContext(), R.string.vendacarro_mensagemsucesso, Toast.LENGTH_SHORT).show();
    }

    private void updateRadioButtons() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Carregando dados...");
        progressDialog.show();

        rg = (RadioGroup) findViewById(R.id.venda_carro_rg);

        carrosUserListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);

                if (user.cars == null) {
                    Toast.makeText(getContext(), R.string.criarvendadialog_erronenhumcarro, Toast.LENGTH_SHORT).show();
                    dismiss();
                } else {
                    for (Carro carro : user.cars) {
                        RadioButton rbn = new RadioButton(getContext());

                        rbn.setId(qtdeCarros);
                        rbn.setText(carro.modelo);

                        idsList.add(user.cars.indexOf(carro));

                        rg.addView(rbn);

                        qtdeCarros++;
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