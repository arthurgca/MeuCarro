package projetoi.meucarro.dialog;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import projetoi.meucarro.LoginActivity;
import projetoi.meucarro.R;
import projetoi.meucarro.models.CarroUser;
import projetoi.meucarro.models.Gasto;

/**
 * Created by Arthur on 13/06/2017.
 */

public class AdicionarGastoDialog extends Dialog {


    private Date dataEscolhida;
    private CarroUser carroUser;
    private DatabaseReference carrosUserRef;
    private String lastCarId;
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;

    public AdicionarGastoDialog(Activity activity) {
        super(activity);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.adicionar_gasto_dialog);
        final Calendar dataAtual = Calendar.getInstance();

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        carrosUserRef = database.getReference().child("users").child(mAuth.getCurrentUser().getUid());

        final Button dataButton = (Button) findViewById(R.id.dialogDataButton);
        Button adcButton = (Button) findViewById(R.id.dialogAdicionar);
        final Spinner dialogSpinner = (Spinner) findViewById(R.id.dialogSpinner);
        final EditText editTextValor = (EditText) findViewById(R.id.dialogValorEdit);
        final EditText editTextKm = (EditText) findViewById(R.id.quilometragemEdit);

        ArrayAdapter dialogAdapter = ArrayAdapter.createFromResource(getContext(), R.array.gastos,
                android.R.layout.simple_spinner_item);

        dialogSpinner.setAdapter(dialogAdapter);

        int mesCorrigido = dataAtual.get(Calendar.MONTH) + 1;

        dataButton.setText(dataAtual.get(Calendar.DAY_OF_MONTH) +"/"+mesCorrigido+"/"+
                dataAtual.get(Calendar.YEAR));
        dataEscolhida = dataAtual.getTime();

        dataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar pagamento = Calendar.getInstance();
                        pagamento.set(year, month, dayOfMonth);
                        if (pagamento.compareTo(dataAtual) > 0) {
                            pagamento = Calendar.getInstance();
                            Toast.makeText(getContext(), R.string.erro_data_gasto,
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            dataButton.setText(dayOfMonth+"/"+month+"/"+year);
                        }
                        dataEscolhida = pagamento.getTime();

                    }
                };

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        getContext(), listener, dataAtual.get(Calendar.YEAR), dataAtual.get(Calendar.MONTH),
                        dataAtual.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();

            }
        });

        adcButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextValor.getText().toString().isEmpty() || editTextKm.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), R.string.valor_gasto_vazio,
                            Toast.LENGTH_SHORT).show();
                } else {
                    int quilometragemNova = Integer.valueOf(editTextKm.getText().toString());
                    if (quilometragemNova <= carroUser.kmRodados) {
                        Toast.makeText(getContext(), R.string.erro_nova_quilometragem,
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Gasto novoGasto = new Gasto(dialogSpinner.getSelectedItem().toString(), dataEscolhida,
                                Float.valueOf(editTextValor.getText().toString()));
                        if (carroUser.listaGastos == null) {
                            carroUser.listaGastos = new ArrayList<>();
                        }
                        carroUser.kmRodados = quilometragemNova;
                        carroUser.listaGastos.add(novoGasto);
                        carrosUserRef.child("carrosList").child(lastCarId).setValue(carroUser);
                        dismiss();
                    }
                }
            }
        });

        super.onCreate(savedInstanceState);
    }

    public void setInfo(CarroUser carroUser, String lastCarId) {
        this.carroUser = carroUser;
        this.lastCarId = lastCarId;
    }
}
