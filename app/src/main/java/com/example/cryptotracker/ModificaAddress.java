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

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Single;

public class ModificaAddress extends Fragment {
    List<Pair<String,String>> addresses;

    ArrayList<ModifyView> arrayList;
    List<String> chain = List.of("Polygon","Ethereum","Solana","Bitcoin","BNB","Avalanche");
    RxDataStore<Preferences> dataStoreRX;
    ModifyViewAdapter numbersArrayAdapter;
    String getStringValue(String Key) {
        Preferences.Key<String> PREF_KEY = PreferencesKeys.stringKey(Key);
        Single<String> value = dataStoreRX.data().firstOrError().map(prefs -> prefs.get(PREF_KEY)).onErrorReturnItem("null");
        return value.blockingGet();
    }
    void populate(){
        for(String x : chain){
            if(!getStringValue(x).equals("null")){
                addresses.add(new Pair<>(x,getStringValue(x)));
            }
        }
    }

    void press(int position){
        getActivity().startActivity(new Intent(getActivity(),ModifyAddresses.class).putExtra("chain",
                addresses.get(position).first).putExtra("address",addresses.get(position).second));
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_modifica_address, container, false);
        DataStoreSingleton dataStoreSingleton = DataStoreSingleton.getInstance();
        if (dataStoreSingleton.getDataStore() == null) {
            dataStoreRX = new RxPreferenceDataStoreBuilder(getActivity(), "wallet").build();
        } else {
            dataStoreRX = dataStoreSingleton.getDataStore();
        }
        dataStoreSingleton.setDataStore(dataStoreRX);


        addresses = new ArrayList<>();
        populate();
        arrayList = new ArrayList<>();
        for(Pair<String, String> x : addresses)
            arrayList.add(new ModifyView(x.first,x.second));
        numbersArrayAdapter = new ModifyViewAdapter(getActivity(), arrayList);
        ListView numbersListView = view.findViewById(R.id.list_modify);
        numbersListView.setAdapter(numbersArrayAdapter);
        numbersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               press(position);
            }
        });
        return view;
    }
}