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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.util.Pair;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.example.cryptotracker.Connections.CovalentAPI;
import com.example.cryptotracker.Connections.RTFirebase;
import com.example.cryptotracker.Supports.AddressesDelete;
import com.example.cryptotracker.Supports.DataStoreSingleton;
import com.example.cryptotracker.Supports.DeleteAddressesAdapter;
import com.example.cryptotracker.Supports.Encrypt;
import com.example.cryptotracker.Supports.ModifyView;
import com.example.cryptotracker.Supports.ModifyViewAdapter;
import com.example.cryptotracker.Supports.NumbersView;
import com.example.cryptotracker.Supports.SharedPreferencesManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.core.Single;

public class CancellaAddress extends Fragment {
    ArrayList<AddressesDelete> arrayList;
    DeleteAddressesAdapter numbersArrayAdapter;
    RTFirebase rtFirebase;
    View rootView;

    void removeKey(String key) {
        rtFirebase.deleteUserAddress(
                Encrypt.hash(SharedPreferencesManager.getString(getContext(), "email", "email")),
                key
        );
    }

    void press(int position) {
        new AlertDialog.Builder(getActivity()).setTitle("Conferma Cancellazione").setMessage("Vuoi confermare la cancellazione?")
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeKey(WalletOverviewFragment.addresses.get(position).first);
                        arrayList.clear();
                        WalletOverviewFragment.addresses.remove(WalletOverviewFragment.addresses.get(position));
                        for (Pair<String, String> x : WalletOverviewFragment.addresses)
                            arrayList.add(new AddressesDelete(x.first, x.second));
                        numbersArrayAdapter.notifyDataSetChanged();
                    }
                }).setNegativeButton(R.string.no, null).setIcon(R.drawable.currency_bitcoin).show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_cancella_address, container, false);
        rtFirebase = new RTFirebase();

        arrayList = new ArrayList<>();

        for (Pair<String, String> x : WalletOverviewFragment.addresses)
            arrayList.add(new AddressesDelete(x.first, x.second));
        numbersArrayAdapter = new DeleteAddressesAdapter(getActivity(), arrayList);
        ListView numbersListView = rootView.findViewById(R.id.listViewx);
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

        for (Pair<String, String> x : WalletOverviewFragment.addresses)
            arrayList.add(new AddressesDelete(x.first, x.second));
        numbersArrayAdapter = new DeleteAddressesAdapter(getActivity(), arrayList);
        ListView numbersListView = rootView.findViewById(R.id.listViewx);
        numbersListView.setAdapter(numbersArrayAdapter);
    }
}