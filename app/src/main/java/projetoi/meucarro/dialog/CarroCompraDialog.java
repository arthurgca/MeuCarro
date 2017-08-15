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
    private Button transferenciaBtn;
    private Carro carro;
    private TextView senhaText;
    private String userAtualId;
    private DatabaseReference dialogRef;
    private ValueEventListener listener;
    private TextView kmsRodadosText;

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
        telefoneText = (TextView) findViewById(R.id.comprarcarrodialog_vendedortelefonetext);
        anoText = (TextView) findViewById(R.id.comprarcarrodialog_carroanotext);
        senhaText = (TextView) findViewById(R.id.comprarcarro_senha);
        kmsRodadosText = (TextView) findViewById(R.id.comprarcarrodialog_kmsrodadostxt);

        load();

        transferenciaBtn = (Button) findViewById(R.id.comprarcarrodialog_transferenciabtn);
        transferenciaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (senhaText.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), R.string.erro_comprardialog_senhavazia, Toast.LENGTH_SHORT).show();
                } else if (!senhaText.getText().toString().equals(venda.senhaConfirmacao)) {
                    Toast.makeText(getContext(), R.string.erro_comprardialog_senhaincorreta, Toast.LENGTH_SHORT).show();
                } else {
                    transferirCarro();
                }
            }
        });

        super.onCreate(savedInstanceState);
    }

    private void load() {
        dialogRef = FirebaseDatabase.getInstance().getReference();

        listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot dsUserVendedor = dataSnapshot.child("users").child(venda.vendedorId);
                vendedor = dsUserVendedor.getValue(User.class);

                userAtualId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DataSnapshot dsUserComprador = dataSnapshot.child("users").child(userAtualId);
                comprador = dsUserComprador.getValue(User.class);

                carro = vendedor.cars.get(Integer.parseInt(venda.carroId));

                kmsRodadosText.setText(kmsRodadosText.getText().toString().concat(String.valueOf(carro.kmRodados)));


                modeloText.setText(modeloText.getText().toString().concat(venda.carroModelo));
                anoText.setText(anoText.getText().toString().concat(venda.carroAno));
                nomeVendedorText.setText(nomeVendedorText.getText().toString().concat(vendedor.name));
                telefoneText.setText(telefoneText.getText().toString().concat(vendedor.phone));

                DataSnapshot carrosDaMarca = dataSnapshot.child("carros").child(carro.marca);
                for (DataSnapshot ids : carrosDaMarca.getChildren()) {
                    if (!ids.getKey().equals("codigoFipeMarca")) {
                        if (ids.child("Modelo").getValue().toString().equals(carro.modelo)) {
                            placeHolderList.addAll(CheckStatus.checaStatus((HashMap) ids.child("Manutenção").getValue(), carro));
                        }
                    }
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

    private void transferirCarro() {
        dialogRef.removeEventListener(listener);
        dismiss();

        comprador.addCar(carro);
        vendedor.cars.remove(carro);
        FirebaseDatabase.getInstance().getReference().child("users").child(userAtualId).setValue(comprador);
        FirebaseDatabase.getInstance().getReference().child("users").child(venda.vendedorId).setValue(vendedor);
        FirebaseDatabase.getInstance().getReference().child("vendas").child(venda.vendedorId).child(venda.carroId).removeValue();
    }

    public void setVenda(Venda venda) {
        this.venda = venda;
    }

}
