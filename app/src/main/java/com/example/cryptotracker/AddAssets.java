package com.example.cryptotracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.datastore.preferences.core.MutablePreferences;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava3.RxDataStore;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.cryptotracker.Supports.DataStoreSingleton;

import java.util.Map;

import io.reactivex.rxjava3.core.Single;


public class AddAssets extends AppCompatActivity {
    RxDataStore<Preferences> dataStoreRX;
    public static Map<String,String> values = Map.of("Bitcoin","btc-mainnet","Ethereum","eth-mainnet","Solana","solana-mainnet",
                                        "Polygon","matic-mainnet","BNB","bsc-mainnet","Avalanche","avalanche-mainnet");
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
    public static String getNet(String value){
        for(Map.Entry<String,String> v : values.entrySet()){
            if(v.getValue().equals(value))
                return v.getKey();
        }
        return null;
    }

    String getStringValue(String Key) {
        Preferences.Key<String> PREF_KEY = PreferencesKeys.stringKey(Key);
        Single<String> value = dataStoreRX.data().firstOrError().map(prefs -> prefs.get(PREF_KEY)).onErrorReturnItem("null");
        return value.blockingGet();
    }
    public boolean putStringValue(String Key, String value){
        boolean returnvalue;
        Preferences.Key<String> PREF_KEY = PreferencesKeys.stringKey(Key);
        Single<Preferences> updateResult =  dataStoreRX.updateDataAsync(prefsIn -> {
            MutablePreferences mutablePreferences = prefsIn.toMutablePreferences();
            mutablePreferences.set(PREF_KEY, value);
            return Single.just(mutablePreferences);
        }).onErrorReturnItem(pref_error);
        returnvalue = updateResult.blockingGet() != pref_error;
        return returnvalue;
    }

    private void GetHTTPRequest(String net, String addr){

        RequestQueue volleyQueue = Volley.newRequestQueue(AddAssets.this);
        String url = "https://api.covalenthq.com/v1/"+net+"/address/"+addr+"/balances_v2/?key=cqt_rQ8GxWCJ4GjfhJc3FJj8Yh6DfbMK";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    putStringValue(getNet(net),addr);
                    Toast.makeText(this,"Indirizzo aggiunto",Toast.LENGTH_LONG).show();
                    AddAssets.this.startActivity(new Intent(AddAssets.this, WalletOverview.class));

                },
                error -> {
                    Toast.makeText(this,"Indirizzo non valido, Riprovare!",Toast.LENGTH_LONG).show();
                    AddAssets.this.startActivity(new Intent(AddAssets.this, AddAssets.class));
                }
        );

        volleyQueue.add(jsonObjectRequest);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_assets);
        Spinner spinner = findViewById(R.id.choices_wallet);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.possible_wallets,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        Button btn = findViewById(R.id.add_wallet);
        DataStoreSingleton dataStoreSingleton = DataStoreSingleton.getInstance();
        if (dataStoreSingleton.getDataStore() == null) {
            dataStoreRX = new RxPreferenceDataStoreBuilder(this, "wallet").build();
        } else {
            dataStoreRX = dataStoreSingleton.getDataStore();
        }
        dataStoreSingleton.setDataStore(dataStoreRX);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = ((EditText) findViewById(R.id.editTextText)).getText().toString();
                String network = spinner.getSelectedItem().toString();
                GetHTTPRequest(values.get(network),address);
            }
        });

    }
}