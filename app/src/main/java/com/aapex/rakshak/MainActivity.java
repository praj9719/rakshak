package com.aapex.rakshak;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toolbar;

import com.aapex.rakshak.object.GpsTracker;
import com.aapex.rakshak.object.Request;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;

import static com.aapex.rakshak.Global.gblClearInputText;
import static com.aapex.rakshak.Global.gblGetInputText;
import static com.aapex.rakshak.Global.gblHideKeyboard;
import static com.aapex.rakshak.Global.gblShowKeyboard;
import static com.aapex.rakshak.Global.gblToast;
import static com.aapex.rakshak.Global.getMapsUrl;

public class MainActivity extends AppCompatActivity {

    public static String TAG = "MainActivity";

    private EditText mName, mPhone, mEmail, mIdentityNum, mPostCode, mAddress, mDetails;
    private TextView mTextAddress, mTextDetails;
    private Button mSubmit, mLogin;

    private DatabaseReference mDatabaseReference;
    private MyTaskListener myLocationTaskListener;

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
    }

    private void submitActivity(){
        askForLocationAccessIfNotGranted(new MyTaskListener() {
            @Override
            public void onTaskSuccess() {
                GpsTracker gpsTracker = new GpsTracker(MainActivity.this);
                double latitude = 0.0, longitude = 0.0;
                if (gpsTracker.canGetLocation()) {
                    latitude = gpsTracker.getLatitude();
                    longitude = gpsTracker.getLongitude();
                }else gpsTracker.showSettingsAlert();
                Log.d(TAG, "onTaskSuccess: " + getMapsUrl(latitude, longitude));
                submitActivity(latitude, longitude);
            }

            @Override
            public void onTaskFailed(String error) {
                gblToast(MainActivity.this, error);
            }
        });
    }

    private void submitActivity(double latitude, double longitude){
        gblHideKeyboard(this);
        Request req = getRequest(latitude, longitude);
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

    private Request getRequest(double latitude, double longitude){
        try {
            String name = gblGetInputText(mName);
            String phone = gblGetInputText(mPhone);
            String email = gblGetInputText(mEmail);
            String identityNum = gblGetInputText(mIdentityNum);
            String postcode = gblGetInputText(mPostCode);
            String address = gblGetInputText(mAddress);
            String category = "Disaster";
            String details = gblGetInputText(mDetails);
            long time = System.currentTimeMillis();
            return new Request(name, phone, email, identityNum, postcode, address, category, details, time, latitude, longitude);
        }catch (Exception e){
            gblToast(this, "Failed!\n"+e.getMessage());
            return null;
        }
    }

    private void askForLocationAccessIfNotGranted(MyTaskListener myTaskListener) {
        myLocationTaskListener = myTaskListener;
        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, Global.MY_REQUEST_CODE_LOCATION);
            }else myLocationTaskListener.onTaskSuccess();
        } catch (Exception e){
            myLocationTaskListener.onTaskFailed(e.getMessage());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Global.MY_REQUEST_CODE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    myLocationTaskListener.onTaskSuccess();
                } else myLocationTaskListener.onTaskFailed("Permission not granted!");
                break;
            default:
                Log.d(TAG, "onActivityResult: Invalid request code " + requestCode);
        }
    }
}