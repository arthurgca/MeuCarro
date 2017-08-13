package projetoi.meucarro;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import projetoi.meucarro.adapters.HomeGastosAdapter;
import projetoi.meucarro.dialog.AdicionarGastoDialog;
import projetoi.meucarro.models.Carro;
import projetoi.meucarro.models.Gasto;
import projetoi.meucarro.models.User;


public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private float autonomia = 0;
    private ListView carrosListView;
    private ArrayList<Gasto> carroGastosList;
    private ArrayAdapter<Gasto> adapter;
    private ValueEventListener carrosUserListener;
    private TextView nomeDoCarroTextView;
    private Carro carro;
    private FirebaseDatabase database;
    private DatabaseReference carrosUserRef;
    private FloatingActionButton fab;
    private TextView qteRodagem;
    private HashMap manutencaoHash;
    private User user;
    private int codigoFipeMarca;
    private Integer codigoFipeModelo;
    private TextView precoFipeTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mAuth = FirebaseAuth.getInstance();

        carrosListView = (ListView) findViewById(R.id.homeListView);

        carroGastosList = new ArrayList<>();
        adapter = new HomeGastosAdapter(this, carroGastosList);

        database = FirebaseDatabase.getInstance();
        carrosUserRef = database.getReference();
        carrosListView.setAdapter(adapter);

        updateListView();

        precoFipeTextView = (TextView) findViewById(R.id.home_preco_fipe);
        nomeDoCarroTextView = (TextView) findViewById(R.id.home_nome_carro);
        qteRodagem = (TextView) findViewById(R.id.qteKmsRodados);

        carrosUserRef.addValueEventListener(carrosUserListener);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    adicionarGastoDialog();
            }
        });

        carrosListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                onDeleteClick(position);
                return false;
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
                user = dataSnapshot.child("users").child(mAuth.getCurrentUser().getUid()).getValue(User.class);
                if (user != null && user.cars != null) {
                    carro = user.currentCar();

                    int anoCorrigido = Integer.valueOf(carro.ano) + 1;

                    fab.setVisibility(View.VISIBLE);
                    nomeDoCarroTextView.setText(carro.modelo.substring(0, 20).concat(" " + anoCorrigido));
                    qteRodagem.setText(String.valueOf(carro.kmRodados));

                    if (carro.listaGastos != null) {
                        for (Gasto gasto : carro.listaGastos) {
                            carroGastosList.add(gasto);
                            adapter.notifyDataSetChanged();
                        }
                    }

                    DataSnapshot carrosDaMarca = dataSnapshot.child("carros").child(carro.marca);
                    for (DataSnapshot ids : carrosDaMarca.getChildren()) {
                        if (!ids.getKey().equals("codigoFipeMarca")) {
                            if (ids.child("Modelo").getValue().toString().equals(carro.modelo)) {
                                codigoFipeModelo = Integer.valueOf(String.valueOf(ids.child("codigoFipe").getValue()));
                                manutencaoHash = (HashMap) ids.child("Manutenção").getValue();
                            }
                        } else {
                            codigoFipeMarca = Integer.valueOf(String.valueOf(ids.getValue()));
                        }
                    }


                    RequestQueue mRequestQueue;
                    Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap
                    Network network = new BasicNetwork(new HurlStack());
                    mRequestQueue = new RequestQueue(cache, network);
                    mRequestQueue.start();
                    String url = "https://fipe-parallelum.rhcloud.com/api/v1/carros/marcas/" +
                            codigoFipeMarca + "/modelos/" + codigoFipeModelo + "/anos/" + anoCorrigido + "-1";

                    JsonObjectRequest jsObjRequest = new JsonObjectRequest
                            (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        precoFipeTextView.setText(response.get("Valor") + " (" +
                                        response.get("MesReferencia") +")");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {

                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.d("Error", error.getLocalizedMessage());

                                }
                            });

                    mRequestQueue.add(jsObjRequest);


                } else {
                    fab.setVisibility(View.INVISIBLE);
                    Toast.makeText(HomeActivity.this, R.string.msg_home_listacarrosvazia,
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
        AdicionarGastoDialog adicionarGastoDialog = new AdicionarGastoDialog(HomeActivity.this);
        adicionarGastoDialog.setInfo(user, manutencaoHash);
        adicionarGastoDialog.show();
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
        } else if (id == R.id.menu_adicionar_carro) {
            Intent it = new Intent(HomeActivity.this, AdicionarCarroActivity.class);
            it.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(it);
        } else if (id == R.id.menu_trocar_carro) {
            Intent it = new Intent(HomeActivity.this, TrocarCarroActivity.class);
            it.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(it);
        } else if (id == R.id.menu_calculadora) {
            Intent it = new Intent(HomeActivity.this, GasCalculatorActivity.class);
            it.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(it);
        } else if (id == R.id.expenses_report_menu) {
            Intent it = new Intent(HomeActivity.this, ExpensesReportActivity.class);
            it.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(it);
        } else if (id == R.id.menu_status_carro) {
            Intent it = new Intent(HomeActivity.this, CarroStatusActivity.class);
            it.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(it);
        } else if (id == R.id.menu_oficina) {
            Intent it = new Intent(HomeActivity.this, OficinasActivity.class);
            it.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(it);
        } else if (id == R.id.menu_compararcarro) {
            Intent it = new Intent(HomeActivity.this, CompararCarroActivity.class);
            it.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(it);
        } else if (id == R.id.menu_profile) {
            Intent it = new Intent(HomeActivity.this, ProfileActivity.class);
            it.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(it);
        } else if (id == R.id.menu_venda_carro) {
            Intent it = new Intent(HomeActivity.this, VendaCarroActivity.class);
            it.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(it);
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveUser(User user) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users");
        ref.child(mAuth.getCurrentUser().getUid()).setValue(user);
    }

    public void onDeleteClick(final int position) {

        final AlertDialog.Builder alert = new AlertDialog.Builder(HomeActivity.this);
        alert.setTitle(R.string.home_removergasto_title);
        alert.setMessage(R.string.home_removergasto_text);
        alert.setPositiveButton(R.string.home_removergasto_confirm, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                user.cars.get(user.lastCarIndex).removeGasto(carroGastosList.get(position));
                Collections.sort(carro.listaGastos, Gasto.compareByData());
                saveUser(user);
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
}
