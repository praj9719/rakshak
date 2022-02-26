package com.aapex.rakshak;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aapex.rakshak.object.Request;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import static com.aapex.rakshak.Global.gblClearInputText;
import static com.aapex.rakshak.Global.gblGetInputText;
import static com.aapex.rakshak.Global.gblHideKeyboard;
import static com.aapex.rakshak.Global.gblShowKeyboard;
import static com.aapex.rakshak.Global.gblToast;

public class MainActivity extends AppCompatActivity {

    public static String TAG = "MainActivity";

    private EditText mName, mPhone, mEmail, mIdentityNum, mPostCode, mAddress, mDetails;
    private TextView mTextAddress, mTextDetails;
    private Button mSubmit, mLogin;

    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mName = findViewById(R.id.am_edit_name);
        mPhone = findViewById(R.id.am_edit_phone);
        mEmail = findViewById(R.id.am_edit_email);
        mIdentityNum = findViewById(R.id.am_edit_identity_num);
        mPostCode = findViewById(R.id.am_edit_postcode);
        mAddress = findViewById(R.id.am_edit_address);
        mDetails = findViewById(R.id.am_edit_details);
        mTextAddress = findViewById(R.id.am_text_address);
        mTextDetails = findViewById(R.id.am_text_details);
        mSubmit = findViewById(R.id.am_button_submit);
        mLogin = findViewById(R.id.am_button_login);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference(Global.fbRequestsRef);

        mTextAddress.setOnClickListener(view -> {mAddress.requestFocus(); gblShowKeyboard(this);});
        mTextDetails.setOnClickListener(view -> {mDetails.requestFocus(); gblShowKeyboard(this);});
        mSubmit.setOnClickListener(view -> submitActivity());
        mLogin.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, LoginActivity.class)));
    }

    @Override
    protected void onStart() {
        super.onStart();
//        new Handler(Looper.getMainLooper()).postDelayed(() ->
//                startActivity(new Intent(MainActivity.this, TestActivity.class)), 1000);
    }

    private void submitActivity(){
        gblHideKeyboard(this);
        Request req = getUserInput();
        gblClearInputText(Arrays.asList(mName, mEmail, mIdentityNum, mAddress, mDetails, mPhone, mPostCode));
        if (req==null) return;
        ProgressDialog mProgressDialog = ProgressDialog.show(MainActivity.this, "", "Please wait...", true);
        String id = mDatabaseReference.push().getKey();
        mDatabaseReference.child(id).setValue(req)
                .addOnSuccessListener(aVoid -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Your request has been recorded!").setPositiveButton("OK", (dialogInterface, i) -> {});
                    builder.show();
                })
                .addOnFailureListener(e -> gblToast(this, "Failed!\n"+e.getMessage()))
                .addOnCompleteListener(task -> mProgressDialog.dismiss());
    }

    private Request getUserInput(){
        try {
            String name = gblGetInputText(mName);
            String email = gblGetInputText(mEmail);
            String identityNum = gblGetInputText(mIdentityNum);
            String address = gblGetInputText(mAddress);
            String details = gblGetInputText(mDetails);
            String tmp = gblGetInputText(mPhone);
            int phone = tmp.isEmpty() ? -1 : Integer.parseInt(tmp);
            tmp = gblGetInputText(mPostCode);
            int postcode = tmp.isEmpty() ? -1 : Integer.parseInt(tmp);
            long time = System.currentTimeMillis();
            double latitude = 0.0, longitude = 0.0;
            return new Request(name, email, identityNum, address, details, phone, postcode, time, latitude, longitude);
        }catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            return null;
        }
    }

}