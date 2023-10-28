package com.example.cryptotracker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.cryptotracker.DataType.DataStoreSingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import io.reactivex.rxjava3.core.Single;


public class AddAssets extends AppCompatActivity {
    RxDataStore<Preferences> dataStoreRX;
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

    private void GetHTTPRequest(){

        RequestQueue volleyQueue = Volley.newRequestQueue(AddAssets.this);
        String url = "https://dog.ceo/api/breeds/image/random";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                  //nel caso la richiesta https funzioni
                },
                error -> {
                    Toast.makeText(this,"Indirizzo non esistente, Riprovare!",Toast.LENGTH_LONG).show();
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
// Create an ArrayAdapter using the string array and a default spinner layout.
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.possible_wallets,
                android.R.layout.simple_spinner_item
        );
// Specify the layout to use when the list of choices appears.
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner.
        spinner.setAdapter(adapter);


        Button btn = findViewById(R.id.add_wallet);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }
}