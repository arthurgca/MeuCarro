package projetoi.meucarro;

import android.content.Intent;
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

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Button cadastroActivityButton;
    private EditText loginEmailEditText;
    private EditText senhaEmailEditText;
    private Button loginButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        loginButton = (Button) findViewById(R.id.loginButton);
        cadastroActivityButton = (Button) findViewById(R.id.cadastroButton);
        loginEmailEditText = (EditText) findViewById(R.id.loginEmailEditText);
        senhaEmailEditText = (EditText) findViewById(R.id.loginSenhaEditText);
        mAuth = FirebaseAuth.getInstance();


        cadastroActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(LoginActivity.this , CadastroActivity.class);
                it.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(it);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = loginEmailEditText.getText().toString();
                String senha = senhaEmailEditText.getText().toString();

                if (email.isEmpty() || senha.isEmpty()) {
                    Toast.makeText(LoginActivity.this, R.string.erro_email_senha_vazio,
                            Toast.LENGTH_SHORT).show();
                } else {

                    mAuth.signInWithEmailAndPassword(email, senha)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    Log.d("Logado", "signInWithEmail:onComplete:" + task.isSuccessful());

                                    // If sign in fails, display a message to the user. If sign in succeeds
                                    // the auth state listener will be notified and logic to handle the
                                    // signed in user can be handled in the listener.
                                    if (!task.isSuccessful()) {
                                        FirebaseAuthException erro = ((FirebaseAuthException) task.getException());
                                        String errorMessage = FormatadorErros.getAuthMessage(erro);
                                        Toast.makeText(LoginActivity.this, errorMessage,
                                                Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                }
            }
        });


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("Logado:", "onAuthStateChanged:signed_in:" + user.getUid());
                    Intent it = new Intent(LoginActivity.this, MainActivity.class);
                    it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(it);
                } else {
                    // User is signed out
                    Log.d("Deslogado", "onAuthStateChanged:signed_out");
                }
            }
        };

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
