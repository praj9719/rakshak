package com.aapex.rakshak.feed;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aapex.rakshak.Global;
import com.aapex.rakshak.R;
import com.aapex.rakshak.object.Request;

import java.util.List;
import java.util.Locale;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.FeedViewHolder> {

    private static final String TAG = "FeedAdapter";
    private Context context;
    private List<Request> mList;

    public FeedAdapter(Context context, List<Request> mList) {
        this.context = context;
        this.mList = mList;
    }

    @NonNull
    @Override
    public FeedAdapter.FeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_feed, parent, false);
        return new FeedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedAdapter.FeedViewHolder holder, int position) {
        Request req = mList.get(position);
        Address mAdd = Global.getAddress(context, req.getLatitude(), req.getLongitude());
        String address = mAdd==null ? "-" : (mAdd.getLocality()+", "+mAdd.getAdminArea()+", "+mAdd.getCountryName());
    }



    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class FeedViewHolder extends RecyclerView.ViewHolder {
        private TextView mCategory, mDate, mAddress, mDetails;
        public FeedViewHolder(@NonNull View itemView) {
            super(itemView);
            mCategory = itemView.findViewById(R.id.lf_category);
            mDate = itemView.findViewById(R.id.lf_date);
            mAddress = itemView.findViewById(R.id.lf_address);
            mDetails = itemView.findViewById(R.id.lf_details);
        }
    }
}
