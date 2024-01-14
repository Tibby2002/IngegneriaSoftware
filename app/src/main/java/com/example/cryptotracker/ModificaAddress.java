package com.example.cryptotracker;

import android.content.Intent;
import android.os.Bundle;

import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava3.RxDataStore;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.cryptotracker.Supports.AddressesDelete;
import com.example.cryptotracker.Supports.DataStoreSingleton;
import com.example.cryptotracker.Supports.DeleteAddressesAdapter;
import com.example.cryptotracker.Supports.ModifyView;
import com.example.cryptotracker.Supports.ModifyViewAdapter;
import com.example.cryptotracker.Supports.NumbersViewAdapter;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Single;

public class ModificaAddress extends Fragment {
    ArrayList<ModifyView> arrayList;
    ModifyViewAdapter numbersArrayAdapter;

    View rootView;

    void press(int position){
        getActivity().startActivity(new Intent(getActivity(),ModifyAddresses.class).putExtra("chain",
                WalletOverviewFragment.addresses.get(position).first).putExtra("address",
                WalletOverviewFragment.addresses.get(position).second));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_modifica_address, container, false);

        arrayList = new ArrayList<>();
        for(Pair<String, String> x : WalletOverviewFragment.addresses)
            arrayList.add(new ModifyView(x.first,x.second));
        numbersArrayAdapter = new ModifyViewAdapter(getActivity(), arrayList);
        ListView numbersListView = rootView.findViewById(R.id.list_modify);
        numbersListView.setAdapter(numbersArrayAdapter);
        numbersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               press(position);
            }
        });
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        arrayList.clear();

        for(Pair<String, String> x : WalletOverviewFragment.addresses)
            arrayList.add(new ModifyView(x.first,x.second));
        numbersArrayAdapter = new ModifyViewAdapter(getActivity(), arrayList);
        ListView numbersListView = rootView.findViewById(R.id.list_modify);
        numbersListView.setAdapter(numbersArrayAdapter);
    }

}