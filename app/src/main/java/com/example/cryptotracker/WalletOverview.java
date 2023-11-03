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
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Single;

public class WalletOverview extends AppCompatActivity {
    List<Pair<String, Pair<Double,Double>>> wallets = new ArrayList<>();
    Double btcValue = 0.;
    Double totValue = 0.;
    ArrayList<NumbersView> arrayList;
    NumbersViewAdapter numbersArrayAdapter;
    int count = 0;
    List<String> chain = List.of("Polygon","Ethereum","Solana","Bitcoin","BNB","Avalanche");
    RxDataStore<Preferences> dataStoreRX;

    private void getUSDPrice(){
        for(NumbersView x : arrayList){
            Double newval = Double.parseDouble(changeToValidFormat(x.getNumberInDigit2().substring(1)))*btcValue;
            x.setmNumberInDigit2(String.format("$%,.2f",newval));
        }
        TextView value = findViewById(R.id.textView5);
        value.setText(String.format("$%,.3f",totValue));
        numbersArrayAdapter.notifyDataSetChanged();
    }
    private void getBTCPrice(){
        RequestQueue volleyQueue = Volley.newRequestQueue(WalletOverview.this);
        String url = "https://api.covalenthq.com/v1/btc-mainnet/address/bc1qxy2kgdygjrsqtzq2n0yrf2493p83kkfjhx0wlh/balances_v2/?key=cqt_rQ8GxWCJ4GjfhJc3FJj8Yh6DfbMK";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,url,null,response -> {
            JSONObject obj = null;
            try {
                String jsonString =  response.get("data").toString();
                obj = new JSONObject(jsonString);
                JSONArray arr = obj.getJSONArray("items");
                btcValue = Double.parseDouble(arr.getJSONObject(0).getString("quote_rate"));
                TextView value = findViewById(R.id.textView5);
                value.setText(String.format("₿%,.3f",totValue/btcValue));
                for(NumbersView x : arrayList){
                    Double newval = Double.parseDouble(changeToValidFormat(x.getNumberInDigit2().substring(1)))/btcValue;
                    x.setmNumberInDigit2(String.format("₿%,.7f",newval));
                }
                numbersArrayAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        },error -> {});
        volleyQueue.add(jsonObjectRequest);
    }
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

    private String changeToValidFormat(String s){
        return s.replaceAll(",","");
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
                                    String symbol = arr.getJSONObject(i).getString("contract_ticker_symbol").equals("null") ? "UNDEFINED" : arr.getJSONObject(i).getString("contract_ticker_symbol");
                                    Double balance = calculateBalance(decimals,arr.getJSONObject(i).getString("balance"));
                                    String temp = arr.getJSONObject(i).getString("pretty_quote");
                                    String valid_string = temp.subSequence(1, temp.length()).toString();
                                    Double value = Double.parseDouble(changeToValidFormat(valid_string).equals("ull") ? "0" : changeToValidFormat(valid_string));
                                    wallets.add(new Pair<>(symbol,new Pair<>(balance,value)));
                                }
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                            count--;
                            if(count == 0){
                                for(Pair<String,Pair<Double,Double>> y : wallets){
                                    arrayList.add(new NumbersView(y.second.first.toString(),y.first,String.format("$%,.2f",y.second.second)));
                                    totValue += y.second.second;
                                }
                                v.notifyDataSetChanged();
                                TextView value = findViewById(R.id.textView5);
                                String r = String.format("$%,.2f",totValue);
                                value.setText(r);
                            }
                        },
                        error -> {
                            Toast.makeText(this, "Errore imprevisto, Riprovare!", Toast.LENGTH_LONG).show();
                            count--;
                            if(count == 0){
                                for(Pair<String,Pair<Double,Double>> y : wallets){
                                    arrayList.add(new NumbersView(y.second.first.toString(),y.first,String.format("$%,.2f",y.second.second)));
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

        arrayList = new ArrayList<>();
        numbersArrayAdapter = new NumbersViewAdapter(this, arrayList);
        populateWallet(arrayList,numbersArrayAdapter);
        ListView numbersListView = findViewById(R.id.listView);
        numbersListView.setAdapter(numbersArrayAdapter);

        FloatingActionButton fab2 = findViewById(R.id.fabs2);
        Switch sw = findViewById(R.id.switch1);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    getBTCPrice();
                }else{
                    getUSDPrice();
                }
            }
        });
        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                WalletOverview.this.startActivity(new Intent(WalletOverview.this, AddAssets.class));
            }
        });
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WalletOverview.this.startActivity(new Intent(WalletOverview.this, WalletModify.class));
            }
        });
    }

    @Override
    protected void onResume () {
        super.onResume();
        arrayList.clear();
        wallets.clear();
        totValue = 0.;
        populateWallet(arrayList,numbersArrayAdapter);
        TextView value = findViewById(R.id.textView5);
        value.setText(totValue.toString());
        numbersArrayAdapter.notifyDataSetChanged();

    }
}