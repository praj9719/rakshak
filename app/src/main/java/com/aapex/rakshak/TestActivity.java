package com.aapex.rakshak;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.aapex.rakshak.Test.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TestActivity extends AppCompatActivity {

    private static final String TAG = "Test Activity";

    private EditText mEmail, mPass;
    private Button mLogin;
    private ProgressBar mProgress;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        mEmail = findViewById(R.id.test_email);
        mPass = findViewById(R.id.test_password);
        mLogin = findViewById(R.id.text_login_button);
        mProgress = findViewById(R.id.test_progress);
        mLogin.setOnClickListener(view -> loginActivity());

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference("User");

    }

    private void loginActivity() {
        myToast("Logging");
        String email = mEmail.getText().toString().trim();
        String password = mPass.getText().toString().trim();
        if (email.isEmpty() || password.isEmpty()) {myToast("Fill all details"); return;}
        mProgress.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> addUserToDatabase("some name", email))
                .addOnFailureListener(e -> myToast("Login Failed!\n" + e.getMessage()))
                .addOnCompleteListener(task -> mProgress.setVisibility(View.INVISIBLE));

    }

    private void addUserToDatabase(String name, String email) {
        String id = mDatabaseReference.push().getKey();
        User user = new User(id, name, email);
        mDatabaseReference.child(id).setValue(user)
                .addOnSuccessListener(aVoid -> myToast("Success"))
                .addOnFailureListener(e -> myToast("Failed to store user details\n" + e.getMessage()))
                .addOnCompleteListener(task -> Log.d(TAG, "addUserToDatabase: Complete"));
    }

    private void myToast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }
}