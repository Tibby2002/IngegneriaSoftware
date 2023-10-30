package com.example.cryptotracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava3.RxDataStore;

import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.cryptotracker.Supports.DataStoreSingleton;
import com.example.cryptotracker.Supports.NumbersView;
import com.example.cryptotracker.Supports.NumbersViewAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Single;

public class WalletOverview extends AppCompatActivity {
    List<Pair<String, Pair<Double,Double>>> wallets = new ArrayList<>();
    Double totValue = 0.;
    int count = 0;
    List<String> chain = List.of("Polygon","Ethereum","Solana","Bitcoin","BNB","Avalanche");
    RxDataStore<Preferences> dataStoreRX;
    String getStringValue(String Key) {
        Preferences.Key<String> PREF_KEY = PreferencesKeys.stringKey(Key);
        Single<String> value = dataStoreRX.data().firstOrError().map(prefs -> prefs.get(PREF_KEY)).onErrorReturnItem("null");
        return value.blockingGet();
    }
    private Double calculateBalance(String decimals, String balance){
        int dec = Integer.parseInt(decimals);
        double bal = Double.parseDouble(balance);
        return bal/Math.pow(10,dec);
    }
    private void populateWallet(ArrayList<NumbersView> arrayList,NumbersViewAdapter v){

        RequestQueue volleyQueue = Volley.newRequestQueue(WalletOverview.this);
        for(String x : chain){
            if(!getStringValue(x).equals("null")){
                count++;
            }
        }
        for(String x : chain) {
            String net = AddAssets.values.get(x);
            if (!getStringValue(x).equals("null")) {
                String url = "https://api.covalenthq.com/v1/" + net + "/address/" + getStringValue(x) + "/balances_v2/?key=cqt_rQ8GxWCJ4GjfhJc3FJj8Yh6DfbMK";
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                        Request.Method.GET,
                        url,
                        null,
                        response -> {
                            JSONObject obj = null;
                            try {
                                String jsonString =  response.get("data").toString();
                                obj = new JSONObject(jsonString);

                                JSONArray arr = obj.getJSONArray("items");
                                for (int i = 0; i < arr.length(); i++)
                                {
                                    String decimals = arr.getJSONObject(i).getString("contract_decimals");
                                    String symbol = arr.getJSONObject(i).getString("contract_ticker_symbol");
                                    Double balance = calculateBalance(decimals,arr.getJSONObject(i).getString("balance"));
                                    String temp = arr.getJSONObject(i).getString("pretty_quote");
                                    Double value = Double.parseDouble(temp.subSequence(1,temp.length()).toString());
                                    wallets.add(new Pair<>(symbol,new Pair<>(balance,value)));
                                }
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                            count--;
                            if(count == 0){
                                for(Pair<String,Pair<Double,Double>> y : wallets){
                                    arrayList.add(new NumbersView(y.second.first.toString(),y.first));
                                    totValue += y.second.second;
                                }
                                v.notifyDataSetChanged();
                                TextView value = findViewById(R.id.textView5);
                                value.setText(totValue.toString());
                            }
                        },
                        error -> {
                            Toast.makeText(this, "Errore imprevisto, Riprovare!"+ url, Toast.LENGTH_LONG).show();
                            count--;
                            if(count == 0){
                                for(Pair<String,Pair<Double,Double>> y : wallets){
                                    arrayList.add(new NumbersView(y.second.first.toString(),y.first));
                                }
                                v.notifyDataSetChanged();
                                TextView value = findViewById(R.id.textView5);
                                value.setText(totValue.toString());
                            }
                        }
                );

                volleyQueue.add(jsonObjectRequest);
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_overview);
        FloatingActionButton fab = findViewById(R.id.fabs);
        DataStoreSingleton dataStoreSingleton = DataStoreSingleton.getInstance();
        if (dataStoreSingleton.getDataStore() == null) {
            dataStoreRX = new RxPreferenceDataStoreBuilder(this, "wallet").build();
        } else {
            dataStoreRX = dataStoreSingleton.getDataStore();
        }
        dataStoreSingleton.setDataStore(dataStoreRX);

        final ArrayList<NumbersView> arrayList = new ArrayList<>();
        NumbersViewAdapter numbersArrayAdapter = new NumbersViewAdapter(this, arrayList);
        populateWallet(arrayList,numbersArrayAdapter);
        ListView numbersListView = findViewById(R.id.listView);
        numbersListView.setAdapter(numbersArrayAdapter);

        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                WalletOverview.this.startActivity(new Intent(WalletOverview.this, WalletOverview.class));
            }
        });
    }
}