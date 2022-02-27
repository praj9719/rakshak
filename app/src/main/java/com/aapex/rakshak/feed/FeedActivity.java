package com.aapex.rakshak.feed;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.aapex.rakshak.Global;
import com.aapex.rakshak.MainActivity;
import com.aapex.rakshak.R;
import com.aapex.rakshak.object.Request;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FeedActivity extends AppCompatActivity {

    private static final String TAG = "FeedActivity";
    private RecyclerView mRecycler;
    private List<Request> mList;
    private RelativeLayout mFilterLayout;
    private Spinner mSpinnerSortBy, mSpinnerDisasterType;
    private Button mFilter;
    private TextView mShowFilter, mEmpty;

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
        mFilter.setOnClickListener(view -> {
            mFilterLayout.setVisibility(View.INVISIBLE);
            loadFeed(true);
        });
        loadFeed(false);
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
        String category = mSpinnerDisasterType.getSelectedItem().toString();

        if (category.equals("All")) return;
        List<Request> newList = new ArrayList<>();
        for (Request req : mList) {
            if (req.getCategory().equals(category)) newList.add(req);
        }
        mList = newList;
    }

}