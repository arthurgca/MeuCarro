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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

import projetoi.meucarro.utils.FormatadorErros;

public class CadastroActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Button cadastroBtn;
    private EditText senhaEditText;
    private EditText emailEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        cadastroBtn = (Button) findViewById(R.id.cadastroButton);
        senhaEditText = (EditText) findViewById(R.id.cadastroSenhaEditText);
        emailEditText = (EditText) findViewById(R.id.cadastroEmailEditText);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    FirebaseAuth.getInstance().signOut();
                    finish();
                    Log.d("Signed in: ", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d("Signed out: ", "onAuthStateChanged:signed_out");
                }
            }
        };

        cadastroBtn.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString();
                String senha = senhaEditText.getText().toString();

                if (email.isEmpty() || email == null || senha.isEmpty() || senha == null) {
                    Toast.makeText(CadastroActivity.this, R.string.mensagemVazio,
                            Toast.LENGTH_SHORT).show();
                } else {

                    mAuth.createUserWithEmailAndPassword(email, senha)
                            .addOnCompleteListener(CadastroActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    Log.d("Log: ", "createUserWithEmail:onComplete:" + task.isSuccessful());

                                    // If sign in fails, display a message to the user. If sign in succeeds
                                    // the auth state listener will be notified and logic to handle the
                                    // signed in user can be handled in the listener.
                                    if (!task.isSuccessful()) {
                                        FirebaseAuthException erro = ((FirebaseAuthException) task.getException());
                                        String errorMessage = FormatadorErros.getAuthMessage(erro);
                                        Toast.makeText(CadastroActivity.this, errorMessage,
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(CadastroActivity.this, R.string.cadastrosucesso,
                                                Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

}
