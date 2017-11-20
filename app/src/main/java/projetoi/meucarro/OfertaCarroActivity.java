package projetoi.meucarro;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import projetoi.meucarro.adapters.HomeGastosAdapter;
import projetoi.meucarro.adapters.StatusRowAdapter;
import projetoi.meucarro.models.Carro;
import projetoi.meucarro.models.Gasto;
import projetoi.meucarro.models.User;
import projetoi.meucarro.models.Venda;
import projetoi.meucarro.utils.CheckStatus;
import projetoi.meucarro.utils.StatusAdapterPlaceholder;

public class OfertaCarroActivity extends AppCompatActivity {

    private TextView modeloText;
    private TextView nomeVendedorText;
    private TextView telefoneText;
    private Venda venda;
    private User vendedor;
    private TextView anoText;
    private StatusRowAdapter statusAdapter;
    private ArrayList<StatusAdapterPlaceholder> placeHolderList;
    private ArrayList<Gasto> carroGastosList;
    private HomeGastosAdapter gastosAdapter;
    private Button confirmarTransferenciaBtn;
    private Carro carro;

    private String userAtualId;
    private DatabaseReference dialogRef;
    private ValueEventListener listener;
    private TextView kmsRodadosText;
    private TextView haOfertaText;
    private Button cancelaOfertaBtn;
    private TextView valorText;
    private String vendedorId;
    private String carroId;
    private ListView gastosListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_oferta_carro);

        vendedorId = getIntent().getStringExtra("vendedorId");
        carroId = getIntent().getStringExtra("carroId");

        placeHolderList = new ArrayList<>();

        ListView statusListView = (ListView) findViewById(R.id.comprarcarrodialog_listview1);
        statusAdapter = new StatusRowAdapter(OfertaCarroActivity.this, placeHolderList);
        statusListView.setAdapter(statusAdapter);

        gastosListView = (ListView) findViewById(R.id.comprarcarrodialog_listView2);
        carroGastosList = new ArrayList<>();
        gastosAdapter = new HomeGastosAdapter(OfertaCarroActivity.this, carroGastosList);
        gastosListView.setAdapter(gastosAdapter);

        modeloText = (TextView) findViewById(R.id.comprarcarrodialog_modelotext);
        nomeVendedorText = (TextView) findViewById(R.id.comprarcarrodialog_vendedornometext);
        valorText = (TextView) findViewById(R.id.comprarcarrodialog_valortext);
        telefoneText = (TextView) findViewById(R.id.comprarcarrodialog_vendedortelefonetext);
        anoText = (TextView) findViewById(R.id.comprarcarrodialog_carroanotext);
        kmsRodadosText = (TextView) findViewById(R.id.comprarcarrodialog_kmsrodadostxt);
        haOfertaText = (TextView) findViewById(R.id.comprarcarrodialog_jaexisteoferta);

        load();

        confirmarTransferenciaBtn = (Button) findViewById(R.id.comprarcarrodialog_confirmarofertabtn);
        confirmarTransferenciaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!venda.haOferta) {
                    if (!venda.compradorId.equals(userAtualId)) {
                        confirmaOferta();
                    }
                }
                else {
                    Toast.makeText(OfertaCarroActivity.this, R.string.compracarrodialog_msgjaexisteoferta,
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        cancelaOfertaBtn = (Button) findViewById(R.id.comprarcarrodialog_cancelarofertabtn);
        cancelaOfertaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (venda.compradorId.equals(userAtualId)) {
                    cancelaOferta();
                } else {
                    Toast.makeText(OfertaCarroActivity.this, "Você não fez nenhuma oferta.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        super.onCreate(savedInstanceState);
    }

    private void cancelaOferta() {
        dialogRef.removeEventListener(listener);
        finish();

        FirebaseDatabase.getInstance().getReference().child("notificacaoOferta").child(venda.vendedorId).removeValue();
        FirebaseDatabase.getInstance().getReference().child("vendas").child(venda.vendedorId).child(venda.carroId).child("compradorId").setValue("");
        FirebaseDatabase.getInstance().getReference().child("vendas").child(venda.vendedorId).child(venda.carroId).child("haOferta").setValue(false);
        Toast.makeText(OfertaCarroActivity.this, R.string.compracarrodialog_msgofertacancelada,
                Toast.LENGTH_LONG).show();
    }

    private void confirmaOferta() {
        dialogRef.removeEventListener(listener);
        finish();

        FirebaseDatabase.getInstance().getReference().child("vendas").child(venda.vendedorId).child(venda.carroId).child("haOferta").setValue(true);
        FirebaseDatabase.getInstance().getReference().child("vendas").child(venda.vendedorId).child(venda.carroId).child("compradorId").setValue(userAtualId);

        FirebaseDatabase.getInstance().getReference().child("notificacaoOferta").child(venda.vendedorId).child("alertaOferta").setValue(true);

        Toast.makeText(OfertaCarroActivity.this, R.string.compracarrodialog_msgsucesso,
                Toast.LENGTH_LONG).show();
    }

    private void load() {
        dialogRef = FirebaseDatabase.getInstance().getReference();

        listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                limparListas();
                venda = dataSnapshot.child("vendas").child(vendedorId).child(carroId).getValue(Venda.class);

                DataSnapshot dsUserVendedor = dataSnapshot.child("users").child(venda.vendedorId);
                vendedor = dsUserVendedor.getValue(User.class);

                userAtualId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                carro = vendedor.cars.get(Integer.parseInt(venda.carroId));

                kmsRodadosText.setText(kmsRodadosText.getText().toString().concat(String.valueOf(carro.kmRodados)));

                valorText.setText(valorText.getText().toString().concat(String.valueOf(venda.valor)));


                modeloText.setText(modeloText.getText().toString().concat(venda.carroModelo));
                anoText.setText(anoText.getText().toString().concat(venda.carroAno));
                nomeVendedorText.setText(nomeVendedorText.getText().toString().concat(vendedor.name));
                telefoneText.setText(telefoneText.getText().toString().concat(vendedor.phone));

                if (venda.haOferta) {
                    if(venda.compradorId.equals(userAtualId)) {
                        haOfertaText.setText(String.valueOf("Você já fez uma oferta por esse carro."));
                    } else {
                        haOfertaText.setText(String.valueOf("Alguém já fez uma oferta por esse carro."));
                    }
                } else {
                    haOfertaText.setText(String.valueOf("Você pode fazer uma oferta."));
                }


                DataSnapshot carrosDaMarca = dataSnapshot.child("carros").child("Padrao").child("0");
                placeHolderList.addAll(CheckStatus.checaStatus((HashMap) carrosDaMarca.child("Manutenção").getValue(), carro));

                if (vendedor.currentCar().listaGastos == null) {
                    vendedor.currentCar().listaGastos = new ArrayList<>();
                    gastosListView.setEnabled(false);

                }
                else {
                    for (Gasto gasto : vendedor.currentCar().listaGastos) {
                        carroGastosList.add(gasto);
                        gastosAdapter.notifyDataSetChanged();
                    }
                }

                statusAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        dialogRef.addValueEventListener(listener);

    }


    private void limparListas() {
        carroGastosList.clear();
        placeHolderList.clear();

        gastosAdapter.notifyDataSetChanged();
        statusAdapter.notifyDataSetChanged();
    }
}
