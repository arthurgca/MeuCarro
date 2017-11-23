package projetoi.meucarro.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import projetoi.meucarro.R;
import projetoi.meucarro.models.Carro;
import projetoi.meucarro.models.User;
import projetoi.meucarro.models.Venda;

/**
 * Created by Arthur on 12/11/2017.
 */

public class ConfirmarVendaDialog extends Dialog {

    private Button confirmarTransferenciaBtn;
    private Venda venda;
    private DatabaseReference dialogRef;
    private User comprador;
    private User vendedor;
    private Carro carro;
    private ValueEventListener listener;
    private TextView nomeCompradorTextView;
    private TextView telefoneCompradorTextView;
    private TextView valorCompraTextView;
    private ProgressDialog progressDialog;


    public ConfirmarVendaDialog(Activity activity) {
        super(activity);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_confirmarvenda);

        confirmarTransferenciaBtn = (Button) findViewById(R.id.confirmarvendadialog_confirmarTransferenciaBtn);

        nomeCompradorTextView = (TextView) findViewById(R.id.confirmarvendadialog_nomecomprador);
        telefoneCompradorTextView = (TextView) findViewById(R.id.confirmarvendadialog_telefonecomprador);
        valorCompraTextView = (TextView) findViewById(R.id.confirmarvendadialog_valorcompra);

        dialogRef = FirebaseDatabase.getInstance().getReference();

        loadInfo();

        Log.d("Venda", String.valueOf(venda.haOferta));

        confirmarTransferenciaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!venda.haOferta) {
                    Toast.makeText(getContext(), R.string.erro_comprardialognaooferta, Toast.LENGTH_SHORT).show();
                }
                else {
                    transferirCarro();
                }
            }
        });
    }


    private void transferirCarro() {
        dialogRef.removeEventListener(listener);
        dismiss();

        comprador.addCar(carro);
        vendedor.cars.remove(carro);
        vendedor.changeCurrentCar(0);


        FirebaseDatabase.getInstance().getReference().child("notificacaoOferta").child(venda.vendedorId).removeValue();
        FirebaseDatabase.getInstance().getReference().child("users").child(venda.compradorId).setValue(comprador);
        FirebaseDatabase.getInstance().getReference().child("users").child(venda.vendedorId).setValue(vendedor);
        FirebaseDatabase.getInstance().getReference().child("vendas").child(venda.vendedorId).child(venda.carroId).removeValue();
        FirebaseDatabase.getInstance().getReference().child("mudancaVenda").child("controle").setValue("MudancaTransfere");

        Toast.makeText(getContext(), R.string.compracarrodialog_msgsucesso,
                Toast.LENGTH_LONG).show();

    }

    public void setVenda(Venda venda) {
        this.venda = venda;
    }

    private void loadInfo() {
        progressDialog.setMessage("Carregando dados...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot dsUserComprador = dataSnapshot.child("users").child(venda.compradorId);
                DataSnapshot dsUserVendedor = dataSnapshot.child("users").child(venda.vendedorId);
                comprador = dsUserComprador.getValue(User.class);
                vendedor = dsUserVendedor.getValue(User.class);

                carregaDadosComprador(comprador);

                carro = vendedor.cars.get(Integer.parseInt(venda.carroId));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        dialogRef.addValueEventListener(listener);
    }

    private void carregaDadosComprador(User comprador) {
        if (venda.haOferta) {
            nomeCompradorTextView.setText(String.format("Nome: %s", comprador.name));
            telefoneCompradorTextView.setText(String.format("Telefone: %s",comprador.phone));
            valorCompraTextView.setText(String.format("Valor da venda: %s", venda.valor));
        }
        progressDialog.dismiss();
    }

    @Override
    protected void onStop() {
        dialogRef.removeEventListener(listener);
        super.onStop();
    }
}
