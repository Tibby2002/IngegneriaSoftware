package com.example.cryptotracker.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.cryptotracker.R;
import com.example.cryptotracker.Supports.PricesOverview;
import com.example.cryptotracker.Supports.PricesOverviewAdapter;
import com.example.cryptotracker.TokenPriceOverview;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HomeFragment extends Fragment {
    PricesOverviewAdapter pricesOverviewAdapter;
    ArrayList<PricesOverview> arrayList;
    ScheduledExecutorService refresh;
    RequestQueue volleyQueue;
    Map<Pair<String,String>,String> prices = new LinkedHashMap<>();
    void populate(){
        prices.put(new Pair<>("eth-mainnet","USDT") ,"0xdAC17F958D2ee523a2206206994597C13D831ec7");
        prices.put(new Pair<>("eth-mainnet","BNB") , "0xB8c77482e45F1F44dE1745F52C74426C631bDD52");
        prices.put(new Pair<>("eth-mainnet","USDC") , "0xA0b86991c6218b36c1d19D4a2e9Eb0cE3606eB48");
        prices.put(new Pair<>("eth-mainnet","stETH") , "0xae7ab96520DE3A18E5e111B5EaAb095312D7fE84");
        prices.put(new Pair<>("eth-mainnet","TRX") , "0x50327c6c5a14DCaDE707ABad2E27eB517df87AB5");
        prices.put(new Pair<>("eth-mainnet","LINK") , "0x514910771AF9Ca656af840dff83E8264EcF986CA");
        prices.put(new Pair<>("eth-mainnet","MATIC") , "0x7D1AfA7B718fb893dB30A3aBc0Cfc608AaCfeBB0");
        prices.put(new Pair<>("eth-mainnet","WBTC") , "0x2260FAC5E5542a773Aa44fBCfeDf7C193bc2C599");
        prices.put(new Pair<>("eth-mainnet","SHIB") , "0x95aD61b0a150d79219dCF64E1E6Cc01f0B64C4cE");
        prices.put(new Pair<>("eth-mainnet","BUSD") , "0x4Fabb145d64652a948d72533023f6E7A623C7C53");
        prices.put(new Pair<>("eth-mainnet","AVAX")  ,"0x85f138bfEE4ef8e540890CFb48F620571d67Eda3");
        prices.put(new Pair<>("eth-mainnet","FTM")  ,"0x4E15361FD6b4BB609Fa63C81A2be19d873717870");
        prices.put(new Pair<>("matic-mainnet","WBTC"),"0x1BFD67037B42Cf73acF2047067bd4F2C47D9BfD6");

    }
    public static  Pair<Boolean,String> dailyChange(double yesterday, double today){
        if(today > yesterday){
            //aumento prezzo maggiore (verde)
            DecimalFormat decimalFormat = new DecimalFormat("###,###.###");
            Double d = ((today-yesterday)/Math.abs(yesterday))*100;
            String format = decimalFormat.format(d)+"%";
            return new Pair<>(true,format);
        }else{
            DecimalFormat decimalFormat = new DecimalFormat("###,###.###");
            Double d = ((yesterday-today)/Math.abs(yesterday))*100;
            String format = decimalFormat.format(d)+"%";
            //decrescente prezoz minore (rosso)
            return new Pair<>(false,format);
        }

    }
    public void populatePrices(ArrayList<PricesOverview> arr){
        populate();
        volleyQueue.cancelAll(request -> true);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime yesterday = LocalDateTime.now().plusDays(-1);
        LocalDateTime now = LocalDateTime.now();
        for(Map.Entry<Pair<String,String>,String> x : prices.entrySet()){
            String url = "https://api.covalenthq.com/v1/pricing/historical_by_addresses_v2/"+ x.getKey().first+"/USD/"+x.getValue()+"/?key=cqt_rQ8GxWCJ4GjfhJc3FJj8Yh6DfbMK"+
                    "&from="+dtf.format(yesterday)+"&to="+dtf.format(now);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,url,null, response -> {
                try{
                    String jsonString =  response.get("data").toString();
                    JSONArray obj = new JSONArray(jsonString);
                    JSONObject obj1 = obj.getJSONObject(0);
                    String curr_price = obj1.getJSONArray("prices").getJSONObject(0).getString("price");
                    String yesterday_price = obj1.getJSONArray("prices").getJSONObject(1).getString("price");
                    Pair<Boolean,String> dailyChange = dailyChange(Double.parseDouble(yesterday_price.replaceAll(",","")),Double.parseDouble(curr_price.replaceAll(",","")));
                    String symbol = obj1.getString("logo_url");
                    String name =  obj1.getString("contract_name");
                    String sigla = obj1.getString("contract_ticker_symbol");
                    DecimalFormat decimalFormat = new DecimalFormat("###,###.#######");
                    arr.add(new PricesOverview(symbol,name,sigla,decimalFormat.format(Double.parseDouble(curr_price)),dailyChange.second,dailyChange.first));
                        pricesOverviewAdapter.notifyDataSetChanged();
                }catch (JSONException e){
                    e.printStackTrace();
                }

            },error -> {});

            volleyQueue.add(jsonObjectRequest);
        }


    }
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chain_overview, container, false);
        arrayList = new ArrayList<>();
        volleyQueue = Volley.newRequestQueue(getContext());
        pricesOverviewAdapter = new PricesOverviewAdapter(getContext(),arrayList);
        populatePrices(arrayList);
        ListView listView = view.findViewById(R.id.list_overview);
        listView.setAdapter(pricesOverviewAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent newIntent = new Intent(getContext(),TokenPriceOverview.class);
                newIntent.putExtra("name",arrayList.get(position).getName());
                newIntent.putExtra("sigla", arrayList.get(position).getSigla());
                newIntent.putExtra("logo",arrayList.get(position).getSymbol());
                newIntent.putExtra("delta",arrayList.get(position).getDeltaPrice());
                String token_addr = "";
                String net = "";
                for(Map.Entry<Pair<String,String>,String> x : prices.entrySet()) {
                    if(x.getKey().second.equals(arrayList.get(position).getSigla())){
                        net = x.getKey().first;
                        token_addr = x.getValue();
                    }
                }
                newIntent.putExtra("price",arrayList.get(position).getPrice());
                newIntent.putExtra("token_address",token_addr);
                newIntent.putExtra("net",net);
                getContext().startActivity(newIntent);
            }
        });
        return view;

    }
    //anche quando viene avviato viene chiamato questo, idem se ritorna dopo la pausa
    @Override
    public void onResume(){
        super.onResume();
        refresh = Executors.newSingleThreadScheduledExecutor();
        refresh.scheduleAtFixedRate(()->{
            arrayList.clear();
            volleyQueue.cancelAll(request -> true);
            populatePrices(arrayList);
        },10,15, TimeUnit.SECONDS);
    }

    //viene chiamato quando viene messo in pausa il fragment
    @Override
    public void onPause(){
        super.onPause();
        refresh.shutdown();
    }

    @Override
    public void onStop(){
        super.onStop();
        refresh.shutdown();
    }
}