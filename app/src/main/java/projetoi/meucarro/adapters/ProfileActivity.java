package projetoi.meucarro.adapters;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import projetoi.meucarro.R;
import projetoi.meucarro.models.User;

public class ProfileActivity extends AppCompatActivity {

    User user;
    EditText name;
    EditText email;
    EditText password;
    EditText phone;
    EditText zipCode;
    Button save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        this.name = (EditText) findViewById(R.id.usernameEditText);
        this.email = (EditText) findViewById(R.id.userEmailEditText);
        this.password = (EditText) findViewById(R.id.userPasswordEditText);
        this.phone = (EditText) findViewById(R.id.userPhoneEditText);
        this.zipCode = (EditText) findViewById(R.id.userZIPCodeEditText);
        this.save = (Button) findViewById(R.id.saveButton);

    }
}
