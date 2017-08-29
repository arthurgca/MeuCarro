package projetoi.meucarro;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import projetoi.meucarro.adapters.StatusRowAdapter;
import projetoi.meucarro.models.Carro;
import projetoi.meucarro.models.User;
import projetoi.meucarro.utils.CheckStatus;
import projetoi.meucarro.utils.StatusAdapterPlaceholder;

public class CarroStatusFragment extends Fragment {

    private DatabaseReference carrosUserRef;
    private String lastCarId;
    private Carro currentCar;
    private FirebaseAuth mAuth;
    private ArrayAdapter adapter;
    private ArrayList<StatusAdapterPlaceholder> placeHolderList;
    private Context act;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_carro_status, container, false);

        act = getActivity();

        mAuth = FirebaseAuth.getInstance();
        placeHolderList = new ArrayList<>();

        ListView listView = (ListView) rootView.findViewById(R.id.carro_status_listview);
        adapter = new StatusRowAdapter(act, placeHolderList);

        listView.setAdapter(adapter);

        ValueEventListener carrosUserListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.child("users").child(mAuth.getCurrentUser().getUid()).getValue(User.class);
                if (user.cars == null) {
                    user.cars = new ArrayList<>();
                }
                currentCar = user.currentCar();

                if (currentCar != null) {
                    if (currentCar.listaGastos != null) {
                        DataSnapshot carrosDaMarca = dataSnapshot.child("carros").child(currentCar.marca);
                        for (DataSnapshot ids : carrosDaMarca.getChildren()) {
                            if (!ids.getKey().toString().equals("codigoFipeMarca")) {
                                if (ids.child("Modelo").getValue().toString().equals(currentCar.modelo)) {
                                    placeHolderList.addAll(CheckStatus.checaStatus((HashMap) ids.child("Manutenção").getValue(), currentCar));
                                }
                            }
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(act, R.string.erro_nenhum_gasto,
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(act, R.string.msg_home_listacarrosvazia,
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Nome", "loadPost:onCancelled", databaseError.toException());
            }
        };

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        this.carrosUserRef = database.getReference();
        this.carrosUserRef.addValueEventListener(carrosUserListener);
        return rootView;
    }

}


