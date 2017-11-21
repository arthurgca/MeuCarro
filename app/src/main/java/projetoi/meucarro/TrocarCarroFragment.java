package projetoi.meucarro;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import projetoi.meucarro.models.Carro;
import projetoi.meucarro.models.User;

public class TrocarCarroFragment extends Fragment {

    private ValueEventListener carrosUserListener;
    private RadioGroup rg;
    private ArrayList<Carro> userCarros;
    private Button trocarCarroBtn;
    private ArrayList<Integer> idsList;
    private int qtdeCarros;
    private User user;
    private Context act;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.activity_trocar_carro, container, false);


        act = getActivity();

        qtdeCarros = 0;
        idsList = new ArrayList<>();

        trocarCarroBtn = (Button) rootView.findViewById(R.id.buttonTrocarCarro);


        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        DatabaseReference carrosUserRef = database.getReference().child("users").child(mAuth.getCurrentUser().getUid());

        updateRadioButtons(rootView);

        carrosUserRef.addValueEventListener(carrosUserListener);

        trocarCarroBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rg.getChildCount() <= 0) {
                    Toast.makeText(getContext(), R.string.erro_trocarcarro_zero_carros, Toast.LENGTH_SHORT).show();
                }

                else if (rg.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(getContext(), R.string.erro_trocarcarro_nenhumaselecao, Toast.LENGTH_SHORT).show();
                }

                else {
                    saveUser(user);
                    Toast.makeText(getContext(), R.string.trocarcarro_msgsucesso, Toast.LENGTH_SHORT).show();

                }
            }
        });
        return rootView;
    }

    private void saveUser(User user) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        user.changeCurrentCar(rg.getCheckedRadioButtonId());


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users");
        ref.child(mAuth.getCurrentUser().getUid()).setValue(user);
        backToHome();

    }

    private void updateRadioButtons(View rootView) {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Carregando dados...");
        progressDialog.show();

        rg = (RadioGroup) rootView.findViewById(R.id.trocar_carro_rg);

        carrosUserListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                limparRg();
                user = dataSnapshot.getValue(User.class);
                if (user.cars != null) {
                    for (Carro carro : user.cars) {
                        RadioButton rbn = new RadioButton(act);

                        rbn.setId(qtdeCarros);
                        rbn.setText(carro.modelo);

                        idsList.add(user.cars.indexOf(carro));

                        rg.addView(rbn);

                        qtdeCarros++;
                    }
                }
                progressDialog.dismiss();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };

    }

    private void limparRg() {
        rg.removeAllViews();
    }

    public void backToHome() {
        HomeFragment homeFragment = new HomeFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, homeFragment);
        fragmentTransaction.commit();
    }

}
