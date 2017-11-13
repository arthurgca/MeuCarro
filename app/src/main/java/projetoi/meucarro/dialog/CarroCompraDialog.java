package projetoi.meucarro.dialog;

import android.app.Activity;
import android.app.Dialog;
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

import projetoi.meucarro.R;
import projetoi.meucarro.adapters.HomeGastosAdapter;
import projetoi.meucarro.adapters.StatusRowAdapter;
import projetoi.meucarro.models.Carro;
import projetoi.meucarro.models.Gasto;
import projetoi.meucarro.models.User;
import projetoi.meucarro.models.Venda;
import projetoi.meucarro.utils.CheckStatus;
import projetoi.meucarro.utils.StatusAdapterPlaceholder;

/**
 * Created by Arthur on 14/08/2017.
 */

public class CarroCompraDialog extends Dialog {

    private TextView modeloText;
    private TextView nomeVendedorText;
    private TextView telefoneText;
    private Venda venda;
    private User vendedor;
    private User comprador;
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

    public CarroCompraDialog(Activity activity) {
        super(activity);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.dialog_comprarcarro);

        placeHolderList = new ArrayList<>();

        ListView statusListView = (ListView) findViewById(R.id.comprarcarrodialog_listViewStatus);
        statusAdapter = new StatusRowAdapter(getContext(), placeHolderList);
        statusListView.setAdapter(statusAdapter);

        ListView gastosListView = (ListView) findViewById(R.id.comprarcarrodialog_listviewgastos);
        carroGastosList = new ArrayList<>();
        gastosAdapter = new HomeGastosAdapter(getContext(), carroGastosList);
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
                    Toast.makeText(getContext(), R.string.compracarrodialog_msgjaexisteoferta,
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
                    Toast.makeText(getContext(), "Você não fez nenhuma oferta.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        super.onCreate(savedInstanceState);
    }

    private void cancelaOferta() {
        dialogRef.removeEventListener(listener);
        dismiss();

        FirebaseDatabase.getInstance().getReference().child("notificacaoOferta").child(venda.vendedorId).removeValue();
        FirebaseDatabase.getInstance().getReference().child("vendas").child(venda.vendedorId).child(venda.carroId).child("compradorId").setValue("");
        FirebaseDatabase.getInstance().getReference().child("vendas").child(venda.vendedorId).child(venda.carroId).child("haOferta").setValue(false);
        Toast.makeText(getContext(), R.string.compracarrodialog_msgofertacancelada,
                Toast.LENGTH_LONG).show();
    }

    private void confirmaOferta() {
        dialogRef.removeEventListener(listener);
        dismiss();

        FirebaseDatabase.getInstance().getReference().child("vendas").child(venda.vendedorId).child(venda.carroId).child("compradorId").setValue(userAtualId);
        FirebaseDatabase.getInstance().getReference().child("vendas").child(venda.vendedorId).child(venda.carroId).child("haOferta").setValue(true);

        FirebaseDatabase.getInstance().getReference().child("notificacaoOferta").child(venda.vendedorId).child("alertaOferta").setValue(true);

        Toast.makeText(getContext(), R.string.compracarrodialog_msgsucesso,
                Toast.LENGTH_LONG).show();
    }

    private void load() {
        dialogRef = FirebaseDatabase.getInstance().getReference();

        listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
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
                }
                for (Gasto gasto : vendedor.currentCar().listaGastos) {
                    carroGastosList.add(gasto);
                    gastosAdapter.notifyDataSetChanged();
                }

                statusAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        dialogRef.addValueEventListener(listener);

    }

    public void setVenda(Venda venda) {
        this.venda = venda;
    }

}
