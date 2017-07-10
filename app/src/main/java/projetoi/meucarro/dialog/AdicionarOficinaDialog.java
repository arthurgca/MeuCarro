package projetoi.meucarro.dialog;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import projetoi.meucarro.R;
import projetoi.meucarro.models.CarroUser;
import projetoi.meucarro.models.Gasto;
import projetoi.meucarro.models.GastoCombustivel;
import projetoi.meucarro.models.Oficina;

public class AdicionarOficinaDialog extends Dialog {

    private DatabaseReference carrosUserRef;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseAuth mAuth;
    private EditText editTextOficina;
    private EditText editTextEndereco;
    private EditText editTextTelefone;
    private RatingBar nota;
    private Button adcButton;

    final DatabaseReference usersRef = database.getReference().child("users");

    public AdicionarOficinaDialog(Activity activity) {
        super(activity);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.adcionar_oficina);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        carrosUserRef = database.getReference().child("users").child(mAuth.getCurrentUser().getUid());

        adcButton = (Button) findViewById(R.id.dialogAdicionar);
        editTextOficina = (EditText) findViewById(R.id.oficinaNomeEdit);
        editTextEndereco = (EditText) findViewById(R.id.enderecoEdit);
        editTextTelefone = (EditText) findViewById(R.id.telefoneEdit);
        nota = (RatingBar) findViewById(R.id.oficinaNota);

        //ArrayAdapter dialogAdapter = ArrayAdapter.createFromResource(getContext(), R.array.adicionardialog_gastosarray,
        //      android.R.layout.simple_spinner_item);

        adcButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextOficina.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), R.string.erro_oficina_nome, Toast.LENGTH_SHORT).show();
                } else if (editTextEndereco.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), R.string.erro_oficina_endereco, Toast.LENGTH_SHORT).show();
                } else if (editTextTelefone.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), R.string.erro_oficina_telefone, Toast.LENGTH_SHORT).show();
                } else {
                    adicionaOficina();
                }
            }
        });

    }

    private void adicionaOficina(){
        Oficina novaOficina;
        novaOficina = new Oficina(editTextOficina.getText().toString(), editTextTelefone.getText().toString(), editTextEndereco.getText().toString(), nota.getRating());
        String uidLastCar = usersRef.child(mAuth.getCurrentUser().getUid()).child("oficinaList").push().getKey();
        usersRef.child(mAuth.getCurrentUser().getUid()).child("oficinaList").child(uidLastCar).setValue(novaOficina);
        dismiss();
    }
}
