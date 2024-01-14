package com.example.cryptotracker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava3.RxDataStore;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.cryptotracker.Connections.CovalentAPI;
import com.example.cryptotracker.Connections.RTFirebase;
import com.example.cryptotracker.Supports.Assets;
import com.example.cryptotracker.Supports.DataStoreSingleton;
import com.example.cryptotracker.Supports.Encrypt;
import com.example.cryptotracker.Supports.NumbersView;
import com.example.cryptotracker.Supports.NumbersViewAdapter;
import com.example.cryptotracker.Supports.SharedPreferencesManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Single;

public class WalletOverviewFragment extends Fragment {
    List<Pair<String, Pair<Double, Double>>> wallets = new ArrayList<>();
    ArrayList<NumbersView> arrayList;
    NumbersViewAdapter numbersArrayAdapter;
    Double totValue = 0.;
    Double btcValue = 0.;
    RTFirebase rtFirebase;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_wallet_overview, container, false);
        FloatingActionButton addNewAddressButton = rootView.findViewById(R.id.addNewAddress);
        rtFirebase = new RTFirebase();
        arrayList = new ArrayList<>();
        numbersArrayAdapter = new NumbersViewAdapter(getContext(), arrayList);
        populateWallet(rootView, arrayList, numbersArrayAdapter);
        ListView numbersListView = rootView.findViewById(R.id.listViewWallet);
        numbersListView.setAdapter(numbersArrayAdapter);

        FloatingActionButton modifyAddressButton = rootView.findViewById(R.id.modifyAddress);
        Switch switchCoinCurr = rootView.findViewById(R.id.switchCoinCurr);
        switchCoinCurr.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    getBTCPrice(rootView);
                } else {
                    getUSDPrice(rootView);
                }
            }
        });

        addNewAddressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddAssets.class);
                startActivity(intent);
            }
        });
        modifyAddressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), WalletModify.class);
                startActivity(intent);
            }
        });

        return rootView;
    }

    private void populateWallet(View rootView, ArrayList<NumbersView> arrayList, NumbersViewAdapter v) {
        rtFirebase.getAllAddresses(
                Encrypt.hash(SharedPreferencesManager.getString(getContext(), "email", "email")),
                new RTFirebase.AddressCallback() {
                    @Override
                    public void onAddressReceived(String key, String address) {
                        CancellaAddress.addresses.add(new Pair<>(key, address));
                        CovalentAPI.getBalanceAsset(getContext(), key, address,
                                new CovalentAPI.OnRequestSuccess() {
                                    @Override
                                    public void onSuccess(JSONObject response) {
                                        JSONObject obj = null;
                                        try {
                                            String jsonString = response.get("data").toString();
                                            obj = new JSONObject(jsonString);

                                            JSONArray arr = obj.getJSONArray("items");
                                            for (int i = 0; i < arr.length(); i++) {
                                                String decimals = arr.getJSONObject(i).getString("contract_decimals");
                                                String symbol = arr.getJSONObject(i).getString("contract_ticker_symbol").equals("null") ? "UNDEFINED" : arr.getJSONObject(i).getString("contract_ticker_symbol");
                                                Double balance = calculateBalance(decimals, arr.getJSONObject(i).getString("balance"));
                                                String temp = arr.getJSONObject(i).getString("pretty_quote");
                                                String valid_string = temp.subSequence(1, temp.length()).toString();
                                                Double value = Double.parseDouble(changeToValidFormat(valid_string).equals("ull") ? "0" : changeToValidFormat(valid_string));
                                                wallets.add(new Pair<>(symbol, new Pair<>(balance, value)));
                                            }
                                        } catch (JSONException e) {
                                            throw new RuntimeException(e);
                                        }
                                        for (Pair<String, Pair<Double, Double>> y : wallets) {
                                            arrayList.add(new NumbersView(y.second.first.toString(), y.first, String.format("$%,.2f", y.second.second)));
                                            totValue += y.second.second;
                                        }
                                        arrayList.sort((o1, o2) -> Double.compare(Double.parseDouble(changeToValidFormat(o2.getNumberInDigit2()).substring(1)), Double.parseDouble(changeToValidFormat(o1.getNumberInDigit2()).substring(1))));
                                        v.notifyDataSetChanged();
                                        TextView valueWallet = rootView.findViewById(R.id.textViewValueWallet);
                                        String r = String.format("$%,.2f", totValue);
                                        valueWallet.setText(r);
                                    }
                                },
                                new CovalentAPI.OnRequestFailure() {
                                    @Override
                                    public void onFailure(VolleyError error) {
                                        Log.d("CovalentAPI getBalanceAsset", error.toString());
                                        Toast.makeText(getContext(), "Errore imprevisto, Riprovare!", Toast.LENGTH_LONG).show();
                                    }
                                }
                        );
                    }

                    @Override
                    public void onNoAddresses() {
                        Log.d("FIREBASE getAllAddresses", "No address found.");
                    }
                });
    }

    private Double calculateBalance(String decimals, String balance) {
        int dec = Integer.parseInt(decimals.equals("null") ? "0" : decimals);
        double bal = Double.parseDouble(balance);
        return bal / Math.pow(10, dec);
    }

    private String changeToValidFormat(String s) {
        return s.replaceAll(",", "");
    }

    private void getBTCPrice(View rootView) {
        RequestQueue volleyQueue = Volley.newRequestQueue(getContext());
        String url = "https://api.covalenthq.com/v1/btc-mainnet/address/bc1qxy2kgdygjrsqtzq2n0yrf2493p83kkfjhx0wlh/balances_v2/?key=cqt_rQ8GxWCJ4GjfhJc3FJj8Yh6DfbMK";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            JSONObject obj = null;
            try {
                String jsonString = response.get("data").toString();
                obj = new JSONObject(jsonString);
                JSONArray arr = obj.getJSONArray("items");
                btcValue = Double.parseDouble(arr.getJSONObject(0).getString("quote_rate"));
                TextView valueWallet = rootView.findViewById(R.id.textViewValueWallet);
                valueWallet.setText(String.format("₿%,.3f", totValue / btcValue));
                for (NumbersView x : arrayList) {
                    Double newval = Double.parseDouble(changeToValidFormat(x.getNumberInDigit2().substring(1))) / btcValue;
                    x.setmNumberInDigit2(String.format("₿%,.7f", newval));
                }
                numbersArrayAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }, error -> {
        });
        volleyQueue.add(jsonObjectRequest);
    }

    private void getUSDPrice(View rootView) {
        for (NumbersView x : arrayList) {
            Double newval = Double.parseDouble(changeToValidFormat(x.getNumberInDigit2().substring(1))) * btcValue;
            x.setmNumberInDigit2(String.format("$%,.2f", newval));
        }
        TextView value = rootView.findViewById(R.id.textViewValueWallet);
        value.setText(String.format("$%,.3f", totValue));
        numbersArrayAdapter.notifyDataSetChanged();
    }
}
