package com.example.cryptotracker.Supports;

import java.util.Map;

public class Assets {

    public static Map<String,String> values = Map.of("Bitcoin","btc-mainnet","Ethereum","eth-mainnet","Solana","solana-mainnet",
            "Polygon","matic-mainnet","BNB","bsc-mainnet","Avalanche","avalanche-mainnet");

    public static String getNet(String value){
        for(Map.Entry<String,String> v : values.entrySet()){
            if(v.getValue().equals(value))
                return v.getKey();
        }
        throw new RuntimeException();
    }



}
