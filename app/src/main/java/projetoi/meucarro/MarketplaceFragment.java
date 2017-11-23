package projetoi.meucarro;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


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
    private ProgressDialog progressDialog;
    private ValueEventListener listener;
    private DatabaseReference dbNotificacaoControle;
    private ValueEventListener listenerControle;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_marketplace, container, false);

        act = getActivity();

        dbRef = FirebaseDatabase.getInstance().getReference();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        dbNotificacaoControle = FirebaseDatabase.getInstance().getReference().child("mudancaVenda");


        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Carregando dados...");
        progressDialog.setCancelable(false);

        listaPessoal = new ArrayList<>();
        listaGlobal = new ArrayList<>();

        spinner = (Spinner) rootView.findViewById(R.id.marketplacefragment_spinner);

        ArrayAdapter spinnerAdapter = ArrayAdapter.createFromResource(getContext(), R.array.distancias,
                android.R.layout.simple_spinner_item);

        spinner.setAdapter(spinnerAdapter);
        spinner.setSelection(0, false);


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

                dbNotificacaoControle.removeEventListener(listenerControle);

            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                progressDialog.show();
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

    private void setControle() {
        listenerControle = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressDialog.show();

                Log.d("Controle", "Entrou");
                loadListas(Double.parseDouble(spinner.getSelectedItem().toString()) * 1000);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        dbNotificacaoControle.addValueEventListener(listenerControle);
    }

    private void loadListas(final double distanciaDada) {

        listener = new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressDialog.show();

                limparListas();

                User userAtual = dataSnapshot.child("users").child(userId).getValue(User.class);

                for (DataSnapshot ds : dataSnapshot.child("vendas").getChildren()) {
                    if (ds.getKey().toString().equals(userId)) {
                        for (DataSnapshot anuncios : ds.getChildren()) {
                            if (!listaPessoal.contains(anuncios.getValue(Venda.class))) {
                                listaPessoal.add(anuncios.getValue(Venda.class));
                            }
                        }
                    } else {
                        for (DataSnapshot anuncios : ds.getChildren()) {
                            final Venda venda = anuncios.getValue(Venda.class);
                            User vendedor = dataSnapshot.child("users").child(venda.vendedorId).getValue(User.class);
                            String url = String.format("http://maps.googleapis.com/maps/api/distancematrix/json?origins=%s" +
                                    "&destinations=%s" +
                                    "&mode=driving&language=pt-BR&sensor=false", userAtual.ZIPcode, vendedor.ZIPcode
                            );
                            StringRequest request = new StringRequest(url, new com.android.volley.Response.Listener<String>() {
                                @Override
                                public void onResponse(String string) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(string);
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
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new com.android.volley.Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError volleyError) {

                                }
                            });

                            RetryPolicy mRetryPolicy = new DefaultRetryPolicy(
                                    0,
                                    0,
                                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

                            request.setRetryPolicy(mRetryPolicy);
                            RequestQueue rQueue = Volley.newRequestQueue(act.getApplicationContext());

                            rQueue.add(request);
                        }
                    }
                }
                progressDialog.dismiss();
                adapterPessoal.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        dbRef.addListenerForSingleValueEvent(listener);

    }


    private void removerAnuncio(final Venda venda) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(act);
        alert.setTitle(R.string.marketplace_removeranunciotitle);
        alert.setMessage(R.string.marketplace_removeranunciomessage);
        alert.setPositiveButton(R.string.home_removergasto_confirm, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseDatabase.getInstance().getReference().child("mudancaVenda").child("controle").setValue("MudançaRemove");
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


    @Override
    public void onResume() {
        setControle();
        super.onResume();
    }

}
