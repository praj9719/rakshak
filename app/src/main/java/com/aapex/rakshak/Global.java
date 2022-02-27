package com.aapex.rakshak;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Global {

    private static final String TAG = "GlobalTag";
    public static int gblNum = 11810477;
    public static String fbRequestsRef = "Requests";
    public static final int MY_REQUEST_CODE_LOCATION = 1181;

    public static void gblToast(Context context, String str){
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }

    public static void gblHideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) view = new View(activity);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void gblShowKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
    }

    public static String gblGetInputText(EditText et){
        return et.getText().toString().trim();
    }

    public static void gblClearInputText(List<EditText> list) {
        for (EditText et : list) et.setText("");
    }

    public static String getMapsUrl(double latitude, double longitude){
        return  "http://maps.google.com/maps?q=" + latitude + "," + longitude;
    }

    public static Address getAddress(Context context, double latitude, double longitude) {
        try {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            return geocoder.getFromLocation(latitude, longitude, 1).get(0);
        } catch (Exception e) {
            Log.d(TAG, "getAddress: " + e.getMessage());
            return null;
        }
    }

}
