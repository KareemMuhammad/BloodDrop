package com.example.blooddrop.UI;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blooddrop.Data.UserViewModel;
import com.example.blooddrop.Models.DonationRequests;
import com.example.blooddrop.R;
import com.example.blooddrop.Util.AdapterRequest;

import java.util.ArrayList;
import java.util.List;

public class RequestListFragment extends Fragment {
   public RequestListFragment (){

   }
    private RecyclerView recyclerView;
    private AdapterRequest adapterRequest;
    private List<DonationRequests> donationRequestsList;
    private RecyclerView.LayoutManager layoutManager;
    private UserViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_request,container,false);
        recyclerView = view.findViewById(R.id.recycler_view);
        adapterRequest = new AdapterRequest();
        layoutManager = new LinearLayoutManager(requireContext());
        recyclerView.setAdapter(adapterRequest);
        recyclerView.setLayoutManager(layoutManager);
        setHasOptionsMenu(true);
        return view;
    }
    public void addItemToList() {
        donationRequestsList = new ArrayList<DonationRequests>();
        viewModel = ViewModelProviders.of(getActivity()).get(UserViewModel.class);
        viewModel.getLiveDataDonation().observe(getActivity(), new Observer<List<DonationRequests>>() {
            @Override
            public void onChanged(List<DonationRequests> donationRequests) {
                donationRequestsList.addAll(donationRequests);
                adapterRequest.setDonationRequests(donationRequestsList);
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        addItemToList();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu,menu);
        MenuItem menuItem = menu.findItem(R.id.app_bar_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("search by name, blood type, organization");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                List<DonationRequests> fakeList = new ArrayList<>();
                for (DonationRequests donationRequests : donationRequestsList){
                    if (donationRequests.getUser().getFull_name().toLowerCase().contains(newText) || donationRequests.getUser().getBlood_type().toLowerCase().contains(newText)
                    || donationRequests.getVehicleLocations().getOrganization().toLowerCase().contains(newText)){
                        fakeList.add(donationRequests);
                    }
                }
                adapterRequest.setDonationRequests(fakeList);
                return true;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }
}
