package com.example.cryptotracker;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;

import com.android.volley.VolleyError;
import com.example.cryptotracker.Connections.CovalentAPI;
import com.example.cryptotracker.Connections.RTFirebase;
import com.example.cryptotracker.Supports.Encrypt;
import com.example.cryptotracker.Supports.SharedPreferencesManager;

import org.json.JSONObject;

public class AddAssets extends AppCompatActivity {

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

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = ((EditText) findViewById(R.id.editTextText)).getText().toString();
                String network = spinner.getSelectedItem().toString();
                CovalentAPI.getBalanceAsset(getApplicationContext(), network, address,
                        new CovalentAPI.OnRequestSuccess() {
                            @Override
                            public void onSuccess(JSONObject response) {
                                RTFirebase rtFirebase = new RTFirebase();
                                rtFirebase.updateUserAddress(
                                        Encrypt.hash(SharedPreferencesManager.getString(getApplicationContext(), "email", "email")),
                                        network, address);
                                Toast.makeText(getApplicationContext(), "Indirizzo aggiunto", Toast.LENGTH_LONG).show();
                                Navigation.findNavController(v).navigate(R.id.nav_wallet);
                            }
                        },
                        new CovalentAPI.OnRequestFailure() {
                            @Override
                            public void onFailure(VolleyError error) {
                                Toast.makeText(getApplicationContext(), "Indirizzo non valido, Riprovare!", Toast.LENGTH_LONG).show();
                            }
                        }
                );
            }
        });

    }
}