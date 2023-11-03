package com.example.cryptotracker;

import static com.example.cryptotracker.AddAssets.getNet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.datastore.preferences.core.MutablePreferences;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava3.RxDataStore;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.cryptotracker.Supports.DataStoreSingleton;

import io.reactivex.rxjava3.core.Single;

public class ModifyAddresses extends AppCompatActivity {

    RxDataStore<Preferences> dataStoreRX;
    public void putStringValue(String Key, String value){
        Preferences.Key<String> PREF_KEY = PreferencesKeys.stringKey(Key);
        Single<Preferences> updateResult =  dataStoreRX.updateDataAsync(prefsIn -> {
            MutablePreferences mutablePreferences = prefsIn.toMutablePreferences();
            mutablePreferences.set(PREF_KEY, value);
            return Single.just(mutablePreferences);
        });
    }

    private void Check_Validate(String net, String addr){

        RequestQueue volleyQueue = Volley.newRequestQueue(this);
        String url = "https://api.covalenthq.com/v1/"+AddAssets.values.get(net)+"/address/"+addr+"/balances_v2/?key=cqt_rQ8GxWCJ4GjfhJc3FJj8Yh6DfbMK";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    putStringValue(net,addr);
                    Toast.makeText(this,"Indirizzo modificato con successo",Toast.LENGTH_LONG).show();
                    this.startActivity(new Intent(this,WalletModify.class));
                },
                error -> {
                    Toast.makeText(this,"Indirizzo non valido, Riprovare!",Toast.LENGTH_LONG).show();
                    this.startActivity(new Intent(this,ModifyAddresses.class).putExtra("chain",
                            getIntent().getExtras().getString("chain")).putExtra("address",getIntent().getExtras().getString("address")));
                }
        );

        volleyQueue.add(jsonObjectRequest);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_addresses);
        DataStoreSingleton dataStoreSingleton = DataStoreSingleton.getInstance();
        if (dataStoreSingleton.getDataStore() == null) {
            dataStoreRX = new RxPreferenceDataStoreBuilder(this, "wallet").build();
        } else {
            dataStoreRX = dataStoreSingleton.getDataStore();
        }
        dataStoreSingleton.setDataStore(dataStoreRX);

        TextView chain = findViewById(R.id.text_chain);
        chain.setText(getIntent().getExtras().getString("chain"));
        TextView addr = findViewById(R.id.text_address);
        addr.setText(getIntent().getExtras().getString("address"));
        Button btn = findViewById(R.id.modify_button);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Check_Validate(chain.getText().toString(),addr.getText().toString());
            }
        });
    }
}