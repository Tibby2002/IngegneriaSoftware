package com.example.cryptotracker.ui.gallery;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;
import com.example.cryptotracker.AddAssets;
import com.example.cryptotracker.MainActivity;
import com.example.cryptotracker.R;
import com.example.cryptotracker.Supports.ForegroundService;
import com.example.cryptotracker.WalletOverview;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class PriceMonitoring extends Fragment {
    Map<String,String> ethereum;
    Map<String,String> polygon;
    Map<String,String> solana;
    Map<String,String> bnb;
    Map<String,String> avalanche;
    void populate(){
        ethereum = new HashMap<>();
        polygon = new HashMap<>();
        solana = new HashMap<>();
        bnb = new HashMap<>();
        avalanche = new HashMap<>();
        ethereum.put("USDT","0xdAC17F958D2ee523a2206206994597C13D831ec7");
        ethereum.put("BNB" , "0xB8c77482e45F1F44dE1745F52C74426C631bDD52");
        ethereum.put("USDC" , "0xA0b86991c6218b36c1d19D4a2e9Eb0cE3606eB48");
        ethereum.put("stETH" , "0xae7ab96520DE3A18E5e111B5EaAb095312D7fE84");
        ethereum.put("TRX" , "0x50327c6c5a14DCaDE707ABad2E27eB517df87AB5");
        ethereum.put("LINK" , "0x514910771AF9Ca656af840dff83E8264EcF986CA");
        ethereum.put("MATIC" , "0x7D1AfA7B718fb893dB30A3aBc0Cfc608AaCfeBB0");
        ethereum.put("WBTC" , "0x2260FAC5E5542a773Aa44fBCfeDf7C193bc2C599");
        ethereum.put("SHIB" , "0x95aD61b0a150d79219dCF64E1E6Cc01f0B64C4cE");
        ethereum.put("BUSD" , "0x4Fabb145d64652a948d72533023f6E7A623C7C53");
        ethereum.put( "AVAX" ,"0x85f138bfEE4ef8e540890CFb48F620571d67Eda3");
        ethereum.put( "FTM" ,"0x4E15361FD6b4BB609Fa63C81A2be19d873717870");

        polygon.put( "BTC" ,"0x1BFD67037B42Cf73acF2047067bd4F2C47D9BfD6");
        polygon.put("USDC" , "0x3c499c542cEF5E3811e1192ce70d8cC03d5c3359");
        polygon.put("USDT" , "0xc2132D05D31c914a87C6611C10748AEb04B58e8F");
        polygon.put("BNB" ,"0x3BA4c387f786bFEE076A58914F5Bd38d668B42c3");
        polygon.put("MATIC" ,"0x0000000000000000000000000000000000001010");
        polygon.put("WBTC" , "0x1BFD67037B42Cf73acF2047067bd4F2C47D9BfD6");
        polygon.put("SHIB" ,"0x6f8a06447Ff6FcF75d803135a7de15CE88C1d4ec");
        polygon.put("BUSD" , "0xdAb529f40E671A1D4bF91361c21bf9f0C9712ab7");
        polygon.put("AVAX" ,"0x2C89bbc92BD86F8075d1DEcc58C7F4E0107f286b");
        polygon.put( "CRO" , "0xAdA58DF0F643D959C2A47c9D4d4c1a4deFe3F11CF");
        polygon.put("FTM" ,"0xC9c1c1c20B3658F8787CC2FD702267791f224Ce1");

        solana.put( "BTC" , "9n4nbM75f5Ui33ZbPYXn59EwSgE8CGsHtAeTH5YFeJ9E");
        solana.put("USDC" , "EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v");
        solana.put("USDT" , "Es9vMFrzaCERmJfrF4H2FYD4KCoNkY11McCe8BenwNYB");
        solana.put("BNB" , "9gP2kCy3wA1ctvYWQk75guqXuHfrEomqydHLtcTCqiLa");
        solana.put("stETH" , "7vfCXTUXx5WJV5JADk17DUJ4ksgau7utNKj4b963voxs");
        solana.put("LINK" , "2wpTofQ8SkACrkZWrZDjXPitYa8AwWgX8AfxdeBRRVLX");
        solana.put("MATIC" , "Gz7VkD4MacbEB6yC5XD3HcumEiYx2EtDYYrfikGsvopG");
        solana.put("BUSD" , "5RpUwQ8wtdPCZHhu6MERp2RGrpobsbZ6MH5dDHkUjs2");
        solana.put("AVAX" , "AUrMpCDYYcPuHhyNX8gEEqbmDPFUpBpHrNW3vPeCFn5Z");
        solana.put( "FTM" ,"EsPKhGTMf3bGoy4Qm7pCv3UCcWqAmbC1UGHBTDxRjjD4");

        bnb.put( "BTC" , "0xfCe146bF3146100cfe5dB4129cf6C82b0eF4Ad8c");
        bnb.put("USDC" , "0x8965349fb649A33a30cbFDa057D8eC2C48AbE2A2");
        bnb.put("BNB" ,"0xbb4CdB9CBd36B01bD1cBaEBF2De08d9173bc095c");
        bnb.put("stETH" , "0x2170Ed0880ac9A755fd29B2688956BD959F933F8");
        bnb.put("TRX" ,"0xCE7de646e7208a4Ef112cb6ed5038FA6cC6b12e3");
        bnb.put("LINK" , "0xF8A0BF9cF54Bb92F17374d9e9A321E6a111a51bD");
        bnb.put("MATIC" ,"0xCC42724C6683B7E57334c4E856f4c9965ED682bD");
        bnb.put("SHIB" ,"0x2859e4544C4bB03966803b044A93563Bd2D0DD4D");
        bnb.put("BUSD" , "0xe9e7CEA3DedcA5984780Bafc599bD69ADd087D56");
        bnb.put("AVAX" , "0x1CE0c2827e2eF14D5C4f29a091d735A204794041");
        bnb.put("FTM" ,"0xAD29AbB318791D579433D831ed122aFeAf29dcfe");

        avalanche.put( "BTC" , "0x152b9d0fdc40c096757f570a51e494bd4b943e50");
        avalanche.put("USDC" , "0xB97EF9Ef8734C71904D8002F8b6Bc66Dd9c48a6E");
        avalanche.put("USDT" , "0xde3A24028580884448a5397872046a019649b084");
        avalanche.put("BNB" , "0x264c1383EA520f73dd837F915ef3a732e204a493");
        avalanche.put("stETH" , "0x49D5c2BdFfac6CE2BFdB6640F4F80f226bc10bAB");
        avalanche.put("LINK" , "0x5947BB275c521040051D82396192181b413227A3");
        avalanche.put("SHIB" , "0x02D980A0D7AF3fb7Cf7Df8cB35d9eDBCF355f665");
        avalanche.put("BUSD" , "0x19860CCB0A68fd4213aB9D8266F7bBf05A8dDe98");
        avalanche.put("AVAX" , "0xB31f66AA3C1e785363F0875A1B74E27b85FD66c7");
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_price_monitoring, container, false);
        Button btn = view.findViewById(R.id.add_notify);
        Spinner spinner = view.findViewById(R.id.input_chain);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.possible_wallets,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        Spinner spinner2 = view.findViewById(R.id.input_token);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(
                getContext(),
                R.array.token_contracts,
                android.R.layout.simple_spinner_item
        );
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter2);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TextInputEditText price = view.findViewById(R.id.input_price);
                String tokenAdress = getTokenAddress(spinner.getSelectedItem().toString(),spinner2.getSelectedItem().toString());
                if(!price.getText().toString().equals("")){
                    verififyValidity(tokenAdress,spinner.getSelectedItem().toString(),price.getText().toString(),spinner2.getSelectedItem().toString());
                }else{
                    Toast.makeText(getContext(),"Prezzo mancante, Riprovare!", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getActivity(),MainActivity.class));
                }
            }
        });

        return view;
    }

    String getTokenAddress(String chain, String token){
        populate();
       switch (chain.toLowerCase()){
           case "polygon":
               return polygon.get(token);
           case "ethereum":
               return ethereum.get(token);
           case "solana":
               return solana.get(token);
           case "bitcoin":
               if(token.equals("WBTC"))
                   return ethereum.get("WBTC");
               return null;
           case "bnb":
               return bnb.get(token);
           case "avalanche":
               return avalanche.get(token);
           default:
               return null;
       }
    }
    void verififyValidity(String token_addr,String chain, String price, String token){
        RequestQueue volleyQueue = Volley.newRequestQueue(getContext());
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime now = LocalDateTime.now();
        String url = "https://api.covalenthq.com/v1/pricing/historical_by_addresses_v2/"+AddAssets.values.get(chain)+"/USD/"+token_addr+"/?key=cqt_rQ8GxWCJ4GjfhJc3FJj8Yh6DfbMK"+
                "&from="+dtf.format(now)+"&to="+dtf.format(now);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,url,null,response -> {
            Toast.makeText(getContext(), "Notifica impostata correttamente!", Toast.LENGTH_LONG).show();
            createNotify(url,price,token);
            startActivity(new Intent(getActivity(),MainActivity.class));

        },error -> {
            Toast.makeText(getContext(),"Argomenti non validi, Riprovare!", Toast.LENGTH_LONG).show();
                startActivity(new Intent(getActivity(), MainActivity.class));
        });

        volleyQueue.add(jsonObjectRequest);
        Toast.makeText(getContext(),"verificando...",Toast.LENGTH_LONG).show();
    }


    void createNotify(String url, String price,String token){
        Intent serviceIntent = new Intent(getContext(), ForegroundService.class);
        serviceIntent.putExtra("url",url);
        serviceIntent.putExtra("price",price);
        serviceIntent.putExtra("token",token);
        getContext().startForegroundService(serviceIntent);
    }


}