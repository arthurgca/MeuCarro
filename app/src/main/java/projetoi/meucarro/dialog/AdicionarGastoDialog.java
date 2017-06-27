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

import static projetoi.meucarro.R.id.dialogSpinner;

public class AdicionarGastoDialog extends Dialog {


    private Date dataEscolhida;
    private CarroUser carroUser;
    private DatabaseReference carrosUserRef;
    private String lastCarId;
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private EditText editTextValor;
    private EditText editTextKm;
    private EditText editTextValorUnidadeCombustivel;
    private Spinner dialogSpinner;
    private Button dataButton;
    private Button adcButton;

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

        dataButton = (Button) findViewById(R.id.dialogDataButton);
        adcButton = (Button) findViewById(R.id.dialogAdicionar);
        dialogSpinner = (Spinner) findViewById(R.id.dialogSpinner);
        editTextValor = (EditText) findViewById(R.id.dialogValorEdit);
        editTextKm = (EditText) findViewById(R.id.quilometragemEdit);
        editTextValorUnidadeCombustivel = (EditText) findViewById(R.id.dialogValorUnidadeCombustivelEditText);


        ArrayAdapter dialogAdapter = ArrayAdapter.createFromResource(getContext(), R.array.adicionardialog_gastosarray,
                android.R.layout.simple_spinner_item);

        dialogSpinner.setAdapter(dialogAdapter);

        final int mesCorrigido = dataAtual.get(Calendar.MONTH) + 1;

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
                        pagamento.set(Calendar.HOUR_OF_DAY, 0);
                        pagamento.set(Calendar.MINUTE, 30);
                        if (pagamento.compareTo(dataAtual) > 0) {
                            pagamento = Calendar.getInstance();
                            Toast.makeText(getContext(), R.string.erro_adicionargasto_dataposterior,
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            dataButton.setText(dayOfMonth+"/"+ (month + 1)+"/"+year);
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
                    Toast.makeText(getContext(), R.string.erro_adicionargasto_vazio,
                            Toast.LENGTH_SHORT).show();
                } else if (editTextValorUnidadeCombustivel.isEnabled() &&
                        editTextValorUnidadeCombustivel.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "Valor do litro/m³ do combustível não pode ser vazio",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Calendar dia = Calendar.getInstance();
                    dia.set(Calendar.HOUR_OF_DAY, 0);
                    dia.set(Calendar.MINUTE, 30);


                    if (dia.getTime().compareTo(dataEscolhida) > 0) {
                        int quilometragemNova = Integer.valueOf(editTextKm.getText().toString());
                            if (quilometragemNova > carroUser.kmRodados && carroUser.kmRodados != 0) {
                            Toast.makeText(getContext(), R.string.erro_adicionargasto_quilmetragem_maior,
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            adicionaGasto(quilometragemNova);
                        }
                    } else {
                        int quilometragemNova = Integer.valueOf(editTextKm.getText().toString());
                        if (quilometragemNova < carroUser.kmRodados) {
                            Toast.makeText(getContext(), R.string.erro_adicionargasto_quilometragem_menor,
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            adicionaGasto(quilometragemNova);
                        }
                    }
                }
            }
        });

        dialogSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String combustivelString = getContext().getResources().getStringArray(R.array.adicionardialog_gastosarray)[0];
                if (dialogSpinner.getSelectedItem().toString().equals(combustivelString)) {
                    editTextValorUnidadeCombustivel.setEnabled(true);
                } else {
                    editTextValorUnidadeCombustivel.setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        super.onCreate(savedInstanceState);
    }

    private void adicionaGasto(int quilometragemNova) {
        Gasto novoGasto;
        if (editTextValorUnidadeCombustivel.isEnabled()) {
            novoGasto = new GastoCombustivel(dialogSpinner.getSelectedItem().toString(), dataEscolhida,
                    Float.valueOf(editTextValor.getText().toString()), quilometragemNova,
                    Float.valueOf(editTextValorUnidadeCombustivel.getText().toString()));
        } else {
            novoGasto = new Gasto(dialogSpinner.getSelectedItem().toString(), dataEscolhida,
                    Float.valueOf(editTextValor.getText().toString()), quilometragemNova);
        }
        Log.d("checa", String.valueOf(checaGastoMenor(novoGasto, carroUser.listaGastos)));
        if (!checaGastoMenor(novoGasto, carroUser.listaGastos)){
            Toast.makeText(getContext(), "Já existe um registro com quilometragem maior em dia anterior.",
                    Toast.LENGTH_SHORT).show();
        } else {

            if (carroUser.listaGastos == null) {
                carroUser.listaGastos = new ArrayList<>();
            }
            if (quilometragemNova >= carroUser.kmRodados) {
                carroUser.kmRodados = quilometragemNova;
            }
            carroUser.listaGastos.add(novoGasto);
            Collections.sort(carroUser.listaGastos, Gasto.compareByData());
            carrosUserRef.child("carrosList").child(lastCarId).setValue(carroUser);
            dismiss();
        }
    }

    public void setInfo(CarroUser carroUser, String lastCarId) {
        this.carroUser = carroUser;
        this.lastCarId = lastCarId;
    }

    private boolean checaGastoMenor(Gasto gasto, List<Gasto> listGastos) {
        boolean podeAdicionar = true;
        Gasto ultimoGasto = null;

        if (listGastos != null) {
            for (Gasto g : listGastos) {
                if (g.data.compareTo(gasto.data) < 0) {
                    ultimoGasto = g;
                }
            }
        }

        if (ultimoGasto != null) {
            if (gasto.registroKm < ultimoGasto.registroKm) {
                Log.d("gastoRegistro", String.valueOf(gasto.registroKm));
                Log.d("ultimoRegistro", String.valueOf(ultimoGasto.registroKm));

                podeAdicionar = false;
            }
        }

        return podeAdicionar;
    }


}
