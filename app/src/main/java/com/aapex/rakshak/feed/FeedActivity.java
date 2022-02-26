package com.aapex.rakshak.feed;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.aapex.rakshak.Global;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        mRecycler = findViewById(R.id.af_recycler);
        mRecycler.setHasFixedSize(true);
        mRecycler.setLayoutManager(new LinearLayoutManager(FeedActivity.this));
        mList = new ArrayList<>();
        loadFeed();
    }

    private void loadFeed(){
        DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference(Global.fbRequestsRef);
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) return;
                mList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    try {
                        Request req = data.getValue(Request.class);
                        mList.add(req);
                    }catch (Exception e){
                        Log.d(TAG, "onDataChange: Error: " + e.getMessage());
                    }
                }
                Log.d(TAG, "onDataChange: size: " + mList.size());
                FeedAdapter feedAdapter = new FeedAdapter(FeedActivity.this, mList);
                mRecycler.setAdapter(feedAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: " + error.getMessage());
            }
        });
    }
}