package projetoi.meucarro;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import projetoi.meucarro.dialog.CarroCompraDialog;
import projetoi.meucarro.dialog.CriarVendaDialog;
import projetoi.meucarro.models.Venda;

public class MarketplaceFragment extends Fragment {

    private ListView listViewSeusAnuncios;
    private ListView listViewAnunciosGlobais;
    private ArrayAdapter<Venda> adapterPessoal;
    private ArrayAdapter<Venda> adapterGlobal;
    private ArrayList<Venda> listaPessoal;
    private ArrayList<Venda> listaGlobal;
    private DatabaseReference dbRef;
    private String userId;
    private Context act;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_marketplace, container, false);

        act = getActivity();

        dbRef = FirebaseDatabase.getInstance().getReference().child("vendas");
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();


        listaPessoal = new ArrayList<>();
        listaGlobal = new ArrayList<>();

        listViewSeusAnuncios = (ListView) rootView.findViewById(R.id.marketplace_listViewSeusAnuncios);
        listViewAnunciosGlobais = (ListView) rootView.findViewById(R.id.marketplace_listViewAnunciosGlobais);

        adapterPessoal = new ArrayAdapter<>(act, android.R.layout.simple_list_item_1, listaPessoal);
        adapterGlobal = new ArrayAdapter<>(act, android.R.layout.simple_list_item_1, listaGlobal);

        listViewSeusAnuncios.setAdapter(adapterPessoal);

        listViewSeusAnuncios.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                removerAnuncio(listaPessoal.get(position));
                return false;
            }
        });

        listViewAnunciosGlobais.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mostrarCarroDialog(listaGlobal.get(position));
            }
        });

        listViewAnunciosGlobais.setAdapter(adapterGlobal);

        loadListas();

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarVendaDialog();
            }
        });
        return rootView;
    }

    private void loadListas() {

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                limparListas();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.getKey().toString().equals(userId)) {
                        for (DataSnapshot anuncios : ds.getChildren()) {
                            listaPessoal.add(anuncios.getValue(Venda.class));
                        }
                    } else {
                        for (DataSnapshot anuncios : ds.getChildren()) {
                            listaGlobal.add(anuncios.getValue(Venda.class));
                        }
                    }
                }
                adapterPessoal.notifyDataSetChanged();
                adapterGlobal.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    private void removerAnuncio(final Venda venda) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(act);
        alert.setTitle(R.string.marketplace_removeranunciotitle);
        alert.setMessage(R.string.marketplace_removeranunciomessage);
        alert.setPositiveButton(R.string.home_removergasto_confirm, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dbRef.child(userId).child(venda.carroId).removeValue();
            }
        });

        alert.setNegativeButton(R.string.home_removergasto_reject, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alert.show();
    }

    private void mostrarCarroDialog(Venda venda) {
        CarroCompraDialog carroCompraDialog = new CarroCompraDialog(getActivity());
        carroCompraDialog.setVenda(venda);
        carroCompraDialog.show();
    }


    private void mostrarVendaDialog() {
        CriarVendaDialog criarVendaDialog = new CriarVendaDialog(getActivity());
        criarVendaDialog.show();
    }

    private void limparListas() {
        listaPessoal.clear();
        listaGlobal.clear();

        adapterGlobal.notifyDataSetChanged();
        adapterPessoal.notifyDataSetChanged();
    }

}
