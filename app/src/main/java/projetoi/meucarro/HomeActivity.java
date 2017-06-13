package projetoi.meucarro;

import android.app.DatePickerDialog;
import android.app.Dialog;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import projetoi.meucarro.models.CarroUser;
import projetoi.meucarro.models.Gasto;


public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private ListView carrosListView;
    private ArrayList<Gasto> carroGastosList;
    private ArrayAdapter<Gasto> adapter;
    private ValueEventListener carrosUserListener;
    private TextView nomeDoCarroTextView;
    private Date dataEscolhida;
    private CarroUser carroUser;
    private FirebaseDatabase database;
    private DatabaseReference carrosUserRef;
    private String lastCarId;
    private FloatingActionButton fab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mAuth = FirebaseAuth.getInstance();

        carrosListView = (ListView) findViewById(R.id.homeListView);

        carroGastosList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, carroGastosList);

        database = FirebaseDatabase.getInstance();
        carrosUserRef = database.getReference().child("users").child(mAuth.getCurrentUser().getUid());
        carrosListView.setAdapter(adapter);

        updateListView();

        nomeDoCarroTextView = (TextView) findViewById(R.id.home_nome_carro);

        carrosUserRef.addValueEventListener(carrosUserListener);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    adicionarGastoDialog();
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
                carroGastosList.clear();
                if (dataSnapshot.child("lastCar").getValue() != null) {
                    fab.setVisibility(View.VISIBLE);
                    lastCarId = dataSnapshot.child("lastCar").getValue().toString();
                    carroUser = dataSnapshot.child("carrosList").child(lastCarId).getValue(CarroUser.class);
                    nomeDoCarroTextView.setText(carroUser.modelo);

                    for (Gasto gasto : carroUser.listaGastos) {
                        carroGastosList.add(gasto);
                        adapter.notifyDataSetChanged();
                    }


                } else {
                    fab.setVisibility(View.INVISIBLE);
                    Toast.makeText(HomeActivity.this, R.string.home_erro_nenhum_carro,
                            Toast.LENGTH_LONG).show();
                }
                progressDialog.dismiss();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Nome", "loadPost:onCancelled", databaseError.toException());
            }
        };
    }

    private void adicionarGastoDialog() {
        final Dialog dialog = new Dialog(HomeActivity.this);
        dialog.setContentView(R.layout.adicionar_gasto_dialog);
        final Calendar dataAtual = Calendar.getInstance();

        final Button dataButton = (Button) dialog.findViewById(R.id.dialogDataButton);
        Button adcButton = (Button) dialog.findViewById(R.id.dialogAdicionar);
        final Spinner dialogSpinner = (Spinner) dialog.findViewById(R.id.dialogSpinner);
        final EditText editTextValor = (EditText) dialog.findViewById(R.id.dialogValorEdit);

        List<String> gastosList = Arrays.asList(new String[]{"Combustível", "Troca de Óleo", "Troca de Pneu"});
        ArrayAdapter<String> dialogAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, gastosList);

        dialogSpinner.setAdapter(dialogAdapter);

        dataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        dataButton.setText(dayOfMonth+"/"+month+"/"+year);
                        Calendar pagamento = Calendar.getInstance();
                        pagamento.set(year, month, dayOfMonth);
                        dataEscolhida = pagamento.getTime();
                    }
                };

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        HomeActivity.this, listener, dataAtual.get(Calendar.YEAR), dataAtual.get(Calendar.MONTH),
                        dataAtual.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();

            }
        });

        adcButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gasto novoGasto = new Gasto(dialogSpinner.getSelectedItem().toString(), dataEscolhida,
                        Float.valueOf(editTextValor.getText().toString()));
                if (carroUser.listaGastos == null) {
                    carroUser.listaGastos = new ArrayList<>();
                }
                carroUser.listaGastos.add(novoGasto);
                carrosUserRef.child("carrosList").child(lastCarId).setValue(carroUser);
                dialog.dismiss();
            }
        });

        dialog.show();
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

        if (id == R.id.menu_adicionar_carro) {
            Intent it = new Intent(HomeActivity.this, AdicionarCarroActivity.class);
            it.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(it);
        }

        if (id == R.id.menu_trocar_carro) {
            Intent it = new Intent(HomeActivity.this, TrocarCarroActivity.class);
            it.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(it);
        }

        if (id == R.id.menu_calculadora) {
            Intent it = new Intent(HomeActivity.this, GasCalculatorActivity.class);
            it.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(it);
        }

        return super.onOptionsItemSelected(item);
    }
}
