package com.example.cryptotracker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.datastore.preferences.core.MutablePreferences;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava3.RxDataStore;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.util.Pair;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.cryptotracker.Supports.AddressesDelete;
import com.example.cryptotracker.Supports.DataStoreSingleton;
import com.example.cryptotracker.Supports.DeleteAddressesAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.core.Single;

public class CancellaAddress extends Fragment {

    Preferences pref_error = new Preferences() {
        @Override
        public <T> boolean contains(@NonNull Key<T> key) {
            return false;
        }

        @Nullable
        @Override
        public <T> T get(@NonNull Key<T> key) {
            return null;
        }

        @NonNull
        @Override
        public Map<Key<?>, Object> asMap() {
            return null;
        }
    };

    List<Pair<String,String>> addresses;

    ArrayList<AddressesDelete> arrayList;
    List<String> chain = List.of("Polygon","Ethereum","Solana","Bitcoin","BNB","Avalanche");
    RxDataStore<Preferences> dataStoreRX;
    DeleteAddressesAdapter numbersArrayAdapter;
    void populate(){
        for(String x : chain){
            if(!getStringValue(x).equals("null")){
                addresses.add(new Pair<>(x,getStringValue(x)));
            }
        }
    }
    void removeKey(String key){
        Preferences.Key<String> PREF_KEY = PreferencesKeys.stringKey(key);
        Single<Preferences> updateResult =  dataStoreRX.updateDataAsync(preferences -> {
          MutablePreferences mtbl = preferences.toMutablePreferences();
          mtbl.remove(PREF_KEY);
          return Single.just(mtbl);
        }).onErrorReturnItem(pref_error);

    }
    void press(int position){
        new AlertDialog.Builder(getActivity()).setTitle("Conferma Cancellazione").setMessage("Vuoi confermare la cancellazione?")
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeKey(addresses.get(position).first);
                        arrayList.clear();
                        addresses.remove(addresses.get(position));
                        for(Pair<String, String> x : addresses)
                            arrayList.add(new AddressesDelete(x.first,x.second));
                        numbersArrayAdapter.notifyDataSetChanged();
                        getActivity().startActivity(new Intent(getActivity(),WalletOverview.class));
                    }
                }).setNegativeButton(R.string.no,null).setIcon(R.drawable.currency_bitcoin).show();
    }
    String getStringValue(String Key) {
        Preferences.Key<String> PREF_KEY = PreferencesKeys.stringKey(Key);
        Single<String> value = dataStoreRX.data().firstOrError().map(prefs -> prefs.get(PREF_KEY)).onErrorReturnItem("null");
        return value.blockingGet();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cancella_address, container, false);
        DataStoreSingleton dataStoreSingleton = DataStoreSingleton.getInstance();
        if (dataStoreSingleton.getDataStore() == null) {
            dataStoreRX = new RxPreferenceDataStoreBuilder(getActivity(), "wallet").build();
        } else {
            dataStoreRX = dataStoreSingleton.getDataStore();
        }

        dataStoreSingleton.setDataStore(dataStoreRX);
        addresses = new ArrayList<>();
        arrayList = new ArrayList<>();
        populate();
        for(Pair<String, String> x : addresses)
            arrayList.add(new AddressesDelete(x.first,x.second));
        numbersArrayAdapter = new DeleteAddressesAdapter(getActivity(), arrayList);
        ListView numbersListView = view.findViewById(R.id.listViewx);
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