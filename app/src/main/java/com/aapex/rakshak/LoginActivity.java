package com.aapex.rakshak;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;

import static com.aapex.rakshak.Global.gblToast;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        EditText mEmail = findViewById(R.id.al_edit_email);
        EditText mPassword = findViewById(R.id.al_edit_password);
        Button mLogin = findViewById(R.id.al_button_login);
        mAuth = FirebaseAuth.getInstance();
        mLogin.setOnClickListener(view -> {
            String email = mEmail.getText().toString().trim();
            String password = mPassword.getText().toString().trim();
            loginActivity(email, password);
        });
    }

    private void loginActivity(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {gblToast(this, "Fill all details"); return;}
        ProgressDialog mProgressDialog = ProgressDialog.show(LoginActivity.this, "", "Please wait...", true);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {gblToast(this, "Login success!");})
                .addOnFailureListener(e -> gblToast(this, e.getMessage()))
                .addOnCompleteListener(task -> mProgressDialog.dismiss());
    }

}