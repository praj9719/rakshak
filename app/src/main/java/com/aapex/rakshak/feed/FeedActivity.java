package com.aapex.rakshak.feed;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.aapex.rakshak.Global;
import com.aapex.rakshak.MainActivity;
import com.aapex.rakshak.MyTaskListener;
import com.aapex.rakshak.R;
import com.aapex.rakshak.object.GpsTracker;
import com.aapex.rakshak.object.Request;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FeedActivity extends AppCompatActivity {

    private static final String TAG = "FeedActivity";
    private RecyclerView mRecycler;
    private List<Request> mList;
    private RelativeLayout mFilterLayout;
    private Spinner mSpinnerSortBy, mSpinnerDisasterType;
    private Button mFilter;
    private TextView mShowFilter, mEmpty;

    private MyTaskListener myLocationTaskListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        mRecycler = findViewById(R.id.af_recycler);
        mRecycler.setHasFixedSize(true);
        mRecycler.setLayoutManager(new LinearLayoutManager(FeedActivity.this));
        mList = new ArrayList<>();
        mFilterLayout = findViewById(R.id.af_layout_filter);
        mSpinnerSortBy = findViewById(R.id.af_spinner_sort_by);
        mSpinnerDisasterType = findViewById(R.id.af_spinner_disaster_type);
        mFilter = findViewById(R.id.af_button_filter);
        mShowFilter = findViewById(R.id.af_show_filter);
        mEmpty = findViewById(R.id.af_empty);
        mShowFilter.setOnClickListener(view -> mFilterLayout.setVisibility(View.VISIBLE));
        mFilterLayout.setOnClickListener(view -> {});
        mFilter.setOnClickListener(view -> {
            mFilterLayout.setVisibility(View.INVISIBLE);
            loadFeed(true);
        });
        loadFeed(false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        askForLocationAccessIfNotGranted(new MyTaskListener() {
            @Override
            public void onTaskSuccess() {

            }

            @Override
            public void onTaskFailed(String error) {

            }
        });
    }

    private void loadFeed(boolean filter){
        ProgressDialog mProgressDialog = ProgressDialog.show(FeedActivity.this, "", "Please wait...", true);
        DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference(Global.fbRequestsRef);
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    mList.clear();
                    for (DataSnapshot data : snapshot.getChildren()) {
                        try {
                            Request req = data.getValue(Request.class);
                            mList.add(req);
                        }catch (Exception e){
                            Log.d(TAG, "onDataChange: Error: " + e.getMessage());
                        }
                    }
                }
                if (filter) applyFilter();
                if (mList.size()==0) mEmpty.setVisibility(View.VISIBLE);
                else mEmpty.setVisibility(View.INVISIBLE);
                FeedAdapter feedAdapter = new FeedAdapter(FeedActivity.this, mList);
                mRecycler.setAdapter(feedAdapter);
                mProgressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: " + error.getMessage());
                mProgressDialog.dismiss();
            }
        });
    }

    private void applyFilter() {
        if (mList==null || mList.isEmpty()) return;
        String sortBy = mSpinnerSortBy.getSelectedItem().toString();

        if (sortBy.equals("Location")) {
            askForLocationAccessIfNotGranted(new MyTaskListener() {
                @Override
                public void onTaskSuccess() {
                    GpsTracker gpsTracker = new GpsTracker(FeedActivity.this);
                    double latitude = 0.0, longitude = 0.0;
                    if (gpsTracker.canGetLocation()) {
                        latitude = gpsTracker.getLatitude();
                        longitude = gpsTracker.getLongitude();
                    }else gpsTracker.showSettingsAlert();
                    double finalLatitude = latitude;
                    double finalLongitude = longitude;
                    Collections.sort(mList, (a, b) -> Integer.compare(dist(finalLatitude, finalLongitude, a.getLatitude(), a.getLongitude()),
                            dist(finalLatitude, finalLongitude, b.getLatitude(), b.getLongitude())));
                }

                @Override
                public void onTaskFailed(String error) {

                }
            });
        }else if(sortBy.equals("Date Des")) Collections.sort(mList, (a, b) -> Long.compare(b.getTime(), a.getTime()));
        else Collections.sort(mList, (a, b) -> Long.compare(a.getTime(), b.getTime()));


        String category = mSpinnerDisasterType.getSelectedItem().toString();

        if (category.equals("All")) return;
        List<Request> newList = new ArrayList<>();
        for (Request req : mList) {
            if (req.getCategory().equals(category)) newList.add(req);
        }
        mList = newList;
    }

    private int dist(double x1, double y1, double x2, double y2) {
        return (int) Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2));
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