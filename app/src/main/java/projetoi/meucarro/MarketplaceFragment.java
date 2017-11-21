package projetoi.meucarro;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.util.ArrayList;
import java.util.HashMap;

import projetoi.meucarro.adapters.VendaAdapter;
import projetoi.meucarro.dialog.ConfirmarVendaDialog;
import projetoi.meucarro.dialog.CriarVendaDialog;
import projetoi.meucarro.models.User;
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
    private double distancia;
    private Spinner spinner;
    OkHttpClient client = new OkHttpClient();
    private ProgressDialog progressDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_marketplace, container, false);

        act = getActivity();

        dbRef = FirebaseDatabase.getInstance().getReference();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();


        listaPessoal = new ArrayList<>();
        listaGlobal = new ArrayList<>();

        spinner = (Spinner) rootView.findViewById(R.id.marketplacefragment_spinner);

        ArrayAdapter spinnerAdapter = ArrayAdapter.createFromResource(getContext(), R.array.distancias,
                android.R.layout.simple_spinner_item);

        spinner.setAdapter(spinnerAdapter);

        listViewSeusAnuncios = (ListView) rootView.findViewById(R.id.marketplace_listViewSeusAnuncios);
        listViewAnunciosGlobais = (ListView) rootView.findViewById(R.id.marketplace_listViewAnunciosGlobais);

        adapterPessoal = new VendaAdapter(act, listaPessoal);
        adapterGlobal = new VendaAdapter(act, listaGlobal);

        listViewSeusAnuncios.setAdapter(adapterPessoal);

        listViewSeusAnuncios.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mostrarOferta(listaPessoal.get(i));
            }
        });

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
                Intent intent = new Intent(getActivity(), OfertaCarroActivity.class);
                intent.putExtra("vendedorId", listaGlobal.get(position).vendedorId);
                intent.putExtra("carroId", listaGlobal.get(position).carroId);

                startActivity(intent);
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                loadListas(Double.parseDouble(spinner.getSelectedItem().toString()) * 1000);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        listViewAnunciosGlobais.setAdapter(adapterGlobal);

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarVendaDialog();
            }
        });
        return rootView;
    }

    private void loadListas(final double distanciaDada) {

        dbRef.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                limparListas();

                adapterPessoal.notifyDataSetChanged();
                adapterGlobal.notifyDataSetChanged();

                HashMap<Venda, String> vendasCep = new HashMap<>();
                User userAtual = dataSnapshot.child("users").child(userId).getValue(User.class);

                for (DataSnapshot ds : dataSnapshot.child("vendas").getChildren()) {
                    if (ds.getKey().toString().equals(userId)) {
                        for (DataSnapshot anuncios : ds.getChildren()) {
                            listaPessoal.add(anuncios.getValue(Venda.class));
                        }
                    } else {
                        for (DataSnapshot anuncios : ds.getChildren()) {
                            Venda venda = anuncios.getValue(Venda.class);
                            User vendedor =  dataSnapshot.child("users").child(venda.vendedorId).getValue(User.class);
                            vendasCep.put(venda, vendedor.ZIPcode);
                        }
                    }
                }
                adicionaEmDistancia(vendasCep, userAtual.ZIPcode,  distanciaDada);

                adapterPessoal.notifyDataSetChanged();
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
                dbRef.child("vendas").child(userId).child(venda.carroId).removeValue();
                dbRef.child("vendas").child("notificacaoOferta").child(userId).removeValue();
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


    private void mostrarVendaDialog() {
        CriarVendaDialog criarVendaDialog = new CriarVendaDialog(getActivity());
        criarVendaDialog.show();
    }

    private void mostrarOferta(Venda venda) {
        ConfirmarVendaDialog carroCompraDialog = new ConfirmarVendaDialog(getActivity());
        carroCompraDialog.setVenda(venda);
        carroCompraDialog.show();
    }

    private void limparListas() {
        listaPessoal.clear();
        listaGlobal.clear();

        adapterGlobal.notifyDataSetChanged();
        adapterPessoal.notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void adicionaEmDistancia(final HashMap<Venda,String> hashVendaCep, String cepComprador, final double distanciaDada) {
        distancia = 0;

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        for (final Venda venda : hashVendaCep.keySet()) {
            String cepVendedor = hashVendaCep.get(venda);
            String url = String.format("http://maps.googleapis.com/maps/api/distancematrix/json?origins=%s" +
                    "&destinations=%s" +
                    "&mode=driving&language=pt-BR&sensor=false", cepComprador, cepVendedor
            );

            try {
                String response = run(url);
                JSONObject jsonObject = new JSONObject(response);
                JSONObject distance =
                        jsonObject.getJSONArray("rows")
                                .getJSONObject(0)
                                .getJSONArray("elements")
                                .getJSONObject(0)
                                .getJSONObject("distance");
                distancia = Double.parseDouble(distance.get("value").toString());
                if (distanciaDada >= distancia) {
                    listaGlobal.add(venda);
                    adapterGlobal.notifyDataSetChanged();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    String run(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }
}
