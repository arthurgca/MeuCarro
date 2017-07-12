package projetoi.meucarro;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import projetoi.meucarro.models.User;
import projetoi.meucarro.utils.FormatadorErros;

public class ProfileActivity extends AppCompatActivity {

    EditText name;
    EditText email;
    EditText password;
    EditText phone;
    EditText zipCode;
    Button save;
    private User user;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        this.mAuth = FirebaseAuth.getInstance();

        this.name = (EditText) findViewById(R.id.usernameEditText);
        this.email = (EditText) findViewById(R.id.userEmailEditText);
        this.password = (EditText) findViewById(R.id.userPasswordEditText);
        this.phone = (EditText) findViewById(R.id.userPhoneEditText);
        this.zipCode = (EditText) findViewById(R.id.userZIPCodeEditText);
        this.save = (Button) findViewById(R.id.saveButton);

        loadUser();

        this.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUser();
            }
        });

    }

    private void loadInfo() {
        this.name.setText(user.name);
        this.email.setText(user.email);
        this.password.setText(user.password);
        this.phone.setText(user.phone);
        this.zipCode.setText(user.ZIPcode);
    }

    private void loadUser() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot
                        .child(mAuth.getCurrentUser().getUid())
                        .getValue(User.class);

                loadInfo();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void saveUser() {
        AuthCredential credential = EmailAuthProvider
                .getCredential(user.email, user.password);


        //re-autentica o usuario para garantir senha
        mAuth.getCurrentUser().reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mAuth.getCurrentUser().updatePassword(password.getText().toString())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        //só atualiza senha se passar na confirmação
                                        user.password = password.getText().toString();

                                    } else {
                                        FirebaseAuthException erro = ((FirebaseAuthException) task.getException());
                                        showErro(erro);
                                    }

                                    mAuth.getCurrentUser().updateEmail(email.getText().toString())
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {

                                                        //só atualiza email se passar na confirmação
                                                        user.email = email.getText().toString();

                                                        user.name = name.getText().toString();
                                                        user.phone = phone.getText().toString();
                                                        user.ZIPcode = zipCode.getText().toString();

                                                        //atualiza user
                                                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users");
                                                        ref.child(mAuth.getCurrentUser().getUid()).setValue(user);

                                                        Toast.makeText(ProfileActivity.this, R.string.perfilactivity_sucesso,
                                                                Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        FirebaseAuthException erro = ((FirebaseAuthException) task.getException());
                                                        showErro(erro);
                                                    }
                                                }
                                            });
                                }
                            });
                        } else {
                            Log.d("TAG", "Error auth failed");
                        }
                    }
                });
    }

    private void showErro(FirebaseAuthException erro) {
        String errorMessage = FormatadorErros.getAuthMessage(erro);
        Toast.makeText(ProfileActivity.this, errorMessage,
                Toast.LENGTH_SHORT).show();
    }
}
