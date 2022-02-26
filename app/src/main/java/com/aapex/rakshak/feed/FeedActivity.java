package com.aapex.rakshak.feed;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

public class FeedActivity extends AppCompatActivity {

    private static final String TAG = "FeedActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        loadFeed();
    }

    private void loadFeed(){
        DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference(Global.fbRequestsRef);
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) return;
                for (DataSnapshot data : snapshot.getChildren()) {
                    try {
                        Request req = data.getValue(Request.class);
                        Log.d(TAG, "onDataChange: " + req.getName() + " " + req.getPhone());
                    }catch (Exception e){
                        Log.d(TAG, "onDataChange: Error: " + e.getMessage());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: " + error.getMessage());
            }
        });
    }
}