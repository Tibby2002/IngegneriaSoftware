package com.example.cryptotracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class WalletOverview extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_overview);
        FloatingActionButton fab = findViewById(R.id.fabs);
        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                WalletOverview.this.startActivity(new Intent(WalletOverview.this, AddAssets.class));
            }
        });
    }
}