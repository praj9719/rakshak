package com.aapex.rakshak.feed;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.aapex.rakshak.Global;
import com.aapex.rakshak.R;
import com.aapex.rakshak.object.Request;

import java.util.List;
import java.util.Locale;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.FeedViewHolder> {

    private static final String TAG = "FeedAdapter";
    private Activity context;
    private List<Request> mList;

    public FeedAdapter(Activity context, List<Request> mList) {
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
        holder.mCategory.setText(req.getCategory());
        holder.mDate.setText(Global.getDate(req.getTime()));
        Address mAdd = Global.getAddress(context, req.getLatitude(), req.getLongitude());
        String address = mAdd==null ? "-" : (mAdd.getLocality()+", "+mAdd.getAdminArea()+", "+mAdd.getCountryName()+". "+mAdd.getPostalCode());
        holder.mAddress.setText(address);
        holder.mDetails.setText(req.getDetails());

        holder.mCard.setOnClickListener(view -> showDetails(req));
    }

    private void showDetails(Request req) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater layoutInflater = context.getLayoutInflater();
        View customView = layoutInflater.inflate(R.layout.popup_request, null);
        TextView name, phone, email, identity, postcode, address;
        name = customView.findViewById(R.id.pop_name);
        phone = customView.findViewById(R.id.pop_phone);
        email = customView.findViewById(R.id.pop_email);
        identity = customView.findViewById(R.id.pop_identity);
        postcode = customView.findViewById(R.id.pop_postcode);
        address = customView.findViewById(R.id.pop_address);

        name.setText(req.getName());
        phone.setText(req.getPhone());
        email.setText(req.getEmail());
        identity.setText(req.getIdentityNum());
        postcode.setText(req.getPostcode());
        address.setText(req.getAddress());

        Button direction = customView.findViewById(R.id.pop_btn_direction);
        direction.setOnClickListener(view -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Global.getMapsUrl(req.getLatitude(), req.getLongitude())));
            context.startActivity(browserIntent);
        });

        builder.setView(customView);
        builder.create();
        builder.show();
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class FeedViewHolder extends RecyclerView.ViewHolder {
        private TextView mCategory, mDate, mAddress, mDetails;
        private CardView mCard;
        public FeedViewHolder(@NonNull View itemView) {
            super(itemView);
            mCategory = itemView.findViewById(R.id.lf_category);
            mDate = itemView.findViewById(R.id.lf_date);
            mAddress = itemView.findViewById(R.id.lf_address);
            mDetails = itemView.findViewById(R.id.lf_details);
            mCard = itemView.findViewById(R.id.lf_card);
        }
    }
}
