package com.aapex.rakshak.feed;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aapex.rakshak.R;
import com.aapex.rakshak.object.Request;

import java.util.List;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.FeedViewHolder> {

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
        holder.mName.setText(req.getName());
        holder.mPhone.setText(String.valueOf(req.getPhone()));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class FeedViewHolder extends RecyclerView.ViewHolder {
        private TextView mName, mPhone;

        public FeedViewHolder(@NonNull View itemView) {
            super(itemView);
            mName = itemView.findViewById(R.id.lf_name);
            mPhone = itemView.findViewById(R.id.lf_phone);
        }
    }
}
