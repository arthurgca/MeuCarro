package projetoi.meucarro;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import java.util.Collections;

import projetoi.meucarro.adapters.OficinaAdapter;
import projetoi.meucarro.dialog.AdicionarOficinaDialog;
import projetoi.meucarro.models.Gasto;
import projetoi.meucarro.models.Oficina;
import projetoi.meucarro.models.User;

public class OficinasFragment extends Fragment {

    private FirebaseAuth mAuth;

    private FloatingActionButton fab;

    private ValueEventListener oficinaListener;
    private DatabaseReference carrosUserRef;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private ArrayList<Oficina> oficinaList;
    private ArrayAdapter<Oficina> adapter;
    private ListView oficinasListView;
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

        oficinasListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                onDeleteClick(i);
                return false;
            }
        });

        oficinaList = new ArrayList<>();
        adapter = new OficinaAdapter(act, oficinaList);

        database = FirebaseDatabase.getInstance();
        carrosUserRef = database.getReference().child("users").child(mAuth.getCurrentUser().getUid());
        oficinasListView.setAdapter(adapter);

        updateListView();

        carrosUserRef.addValueEventListener(oficinaListener);

        return rootView;

    }

    private void adicionarOficina() {
        AdicionarOficinaDialog adicionarOficinaDialog = new AdicionarOficinaDialog(getActivity());
        adicionarOficinaDialog.setInfo(user);
        adicionarOficinaDialog.show();
    }

    private void updateListView() {

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


    private void saveUser(User user) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users");
        ref.child(mAuth.getCurrentUser().getUid()).setValue(user);
    }

    public void onDeleteClick(final int position) {

        final AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle(R.string.oficina_dialog_removertitle);
        alert.setMessage(R.string.oficina_dialog_removetext);
        alert.setPositiveButton(R.string.home_removergasto_confirm, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                user.repairShops.remove(oficinaList.get(position));
                saveUser(user);
                oficinaList.remove(oficinaList.get(position));
                adapter.notifyDataSetChanged();
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


