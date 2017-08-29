package projetoi.meucarro;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import projetoi.meucarro.adapters.OficinaAdapter;
import projetoi.meucarro.dialog.AdicionarOficinaDialog;
import projetoi.meucarro.models.Oficina;
import projetoi.meucarro.models.User;

public class OficinasFragment extends Fragment {

    private DatabaseReference userRef;
    private FirebaseAuth mAuth;

    private FloatingActionButton fab;

    private ValueEventListener oficinaListener;
    private ArrayList<Oficina> oficinasList;
    private String lastCarId;
    private Oficina oficina;
    private DatabaseReference carrosUserRef;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private ArrayList<Oficina> oficinaList;
    private ArrayAdapter<Oficina> adapter;
    private ListView oficinasListView;

    private TextView nomeOficinaTextView;
    private TextView enderecoOficinaTextView;
    private TextView telefoneOficinaTextView;
    private RatingBar notaOficinaRatingBar;
    private User user;
    private Context act;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_oficinas, container, false);

        act = getActivity();

        mAuth = FirebaseAuth.getInstance();

        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adicionarOficina();
            }
        });

        oficinasListView = (ListView) rootView.findViewById(R.id.oficinasListView);

        oficinaList = new ArrayList<>();
        oficinasList = new ArrayList<>();
        adapter = new OficinaAdapter(act, oficinaList);

        database = FirebaseDatabase.getInstance();
        carrosUserRef = database.getReference().child("users").child(mAuth.getCurrentUser().getUid());
        oficinasListView.setAdapter(adapter);

        updateListView();

        nomeOficinaTextView = (TextView) rootView.findViewById(R.id.oficina_nome);
        enderecoOficinaTextView = (TextView) rootView.findViewById(R.id.oficina_endereco);
        telefoneOficinaTextView = (TextView) rootView.findViewById(R.id.oficina_telefone);
        notaOficinaRatingBar = (RatingBar) rootView.findViewById(R.id.oficinaNota);

        carrosUserRef.addValueEventListener(oficinaListener);
        return rootView;
    }

    private void adicionarOficina() {
        AdicionarOficinaDialog adicionarOficinaDialog = new AdicionarOficinaDialog(getActivity());
        adicionarOficinaDialog.setInfo(user);
        adicionarOficinaDialog.show();
    }

    private void updateListView() {
        /*final ProgressDialog progressDialog = new ProgressDialog(OficinasFragment.this);
        progressDialog.setMessage("Carregando dados...");
        progressDialog.show();*/


        oficinaListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                oficinaList.clear();
                fab.setVisibility(View.VISIBLE);
                if (user.repairShops != null) {
                    for (Oficina ofici : user.repairShops) {
                        oficinaList.add(ofici);
                        adapter.notifyDataSetChanged();
                    }
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Nome", "loadPost:onCancelled", databaseError.toException());
            }
        };
    }

}


