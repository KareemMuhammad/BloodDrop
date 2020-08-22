package com.example.blooddrop.Util;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blooddrop.Models.DonationRequests;
import com.example.blooddrop.R;

import java.util.List;

public class AdapterRequest extends RecyclerView.Adapter<AdapterRequest.ViewHolder> {
    private List<DonationRequests> donationRequests;

    public AdapterRequest(List<DonationRequests> donationRequests) {
        this.donationRequests = donationRequests;
    }
    public AdapterRequest(){

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_request,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        DonationRequests textView = donationRequests.get(position);
        holder.data1.setText(textView.getVehicleLocations().getCity());
        holder.data2.setText(textView.getVehicleLocations().getOrganization());
        holder.data3.setText(textView.getDate().toString());
        holder.data4.setText(textView.getUser().getFull_name());
        holder.data5.setText(String.valueOf(textView.getUser().getPhone_no()));
        holder.data6.setText(textView.getUser().getBlood_type());
        holder.data7.setText(textView.getCheckedItem());
    }

    @Override
    public int getItemCount() {
        if (donationRequests == null) return 0;
        return donationRequests.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {

        private TextView data1,data2,data3,data4,data5,data6,data7;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            data1 = itemView.findViewById(R.id.text_view1);
            data2 = itemView.findViewById(R.id.text_view2);
            data3 = itemView.findViewById(R.id.text_view3);
            data4 = itemView.findViewById(R.id.text_view4);
            data5 = itemView.findViewById(R.id.text_view5);
            data6 = itemView.findViewById(R.id.text_view6);
            data7 = itemView.findViewById(R.id.text_view7);
        }
    }

    public void setDonationRequests(List<DonationRequests> donationRequestsList) {
        donationRequests = donationRequestsList;
        notifyDataSetChanged();
    }
}
