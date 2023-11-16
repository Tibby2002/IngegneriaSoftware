package com.example.cryptotracker;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.cryptotracker.Supports.DataStoreSingleton;
import com.example.cryptotracker.Supports.NumbersView;
import com.example.cryptotracker.Supports.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava3.RxDataStore;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cryptotracker.databinding.ActivityMainBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Single;

public class MainActivity extends AppCompatActivity {
    private User user;
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    RxDataStore<Preferences> dataStoreRX;

    List<String> chain = List.of("Polygon","Ethereum","Solana","Bitcoin","BNB","Avalanche");

    public void showSettings(MenuItem item){
        MainActivity.this.startActivity(new Intent(MainActivity.this, Register.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DataStoreSingleton dataStoreSingleton = DataStoreSingleton.getInstance();
        if (dataStoreSingleton.getDataStore() == null) {
            dataStoreRX = new RxPreferenceDataStoreBuilder(this, "wallet").build();
        } else {
            dataStoreRX = dataStoreSingleton.getDataStore();
        }
        dataStoreSingleton.setDataStore(dataStoreRX);
        Boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getBoolean("isFirstRun", true);

        if (isFirstRun) {
            MainActivity.this.startActivity(new Intent(MainActivity.this, Register.class));
        }
        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putBoolean("isFirstRun", false).commit();


        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);

        setSupportActionBar(binding.appBarMain.toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.this.startActivity(new Intent(MainActivity.this, WalletOverview.class));
            }
        });

        String name = " ", surname = " ", photo = " ";

        try {
            File path = this.getFilesDir();
            File file = new File(path, "settings.txt");

            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            int count = 0;
            while ((line = br.readLine()) != null) {
                switch (count) {
                    case 0:
                        name = line;
                        break;
                    case 1:
                        surname = line;
                    case 2:
                        photo = line;
                        break;
                }
                count++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        String usr = name + " " + surname;
        ImageView img = (ImageView) findViewById(R.id.imageView);
        if (!photo.equals("Default-photo") && !photo.equals(" ")) {
            img.setImageIcon(Icon.createWithContentUri(photo));
        }
        NavigationView nav = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.textNome);
        TextView overall = findViewById(R.id.value);
        calculateTotValue();
        navUsername.setText(usr);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void calculateTotValue(){
        int[] count = {0};
        RequestQueue volleyQueue = Volley.newRequestQueue(this);
        for(String x : chain){
            if(!getStringValue(x).equals("null")){
                count[0]++;
            }
        }
        for(String x : chain) {
            String net = AddAssets.values.get(x);
            List<Double> values = new ArrayList<>();
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
                                    String temp = arr.getJSONObject(i).getString("pretty_quote");
                                    String valid_string = temp.subSequence(1, temp.length()).toString();
                                    Double value = Double.parseDouble(valid_string.replaceAll(",","").equals("ull") ? "0" : valid_string.replaceAll(",",""));
                                    values.add(value);
                                }
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                            count[0]--;
                            if(count[0] == 0){
                                Double tot = 0.;
                                for(Double y : values){
                                    tot += y;
                                }
                                TextView value = findViewById(R.id.value);
                                String r = String.format("$%,.2f",tot);
                                value.setText(r);
                            }
                        },
                        error -> {
                            count[0]--;
                            if(count[0] == 0){
                                Double tot = 0.;
                                for(Double y : values){
                                    tot += y;
                                }
                                TextView value = findViewById(R.id.value);
                                String r = String.format("$%,.2f",tot);
                                value.setText(r);
                            }
                        }
                );

                volleyQueue.add(jsonObjectRequest);
            }
        }
    }
    String getStringValue(String Key) {
        Preferences.Key<String> PREF_KEY = PreferencesKeys.stringKey(Key);
        Single<String> value = dataStoreRX.data().firstOrError().map(prefs -> prefs.get(PREF_KEY)).onErrorReturnItem("null");
        return value.blockingGet();
    }
}