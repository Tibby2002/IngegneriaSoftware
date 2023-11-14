package com.example.cryptotracker.Supports;

public class PricesOverview {
    private String symbol;
    private String name;

    private String sigla;
    private String price;

    private String deltaPrice;
    private boolean upper;

   public PricesOverview(String s, String n, String sj, String p, String dp,boolean upp){
       symbol = s;
       name = n;
       sigla = sj;
       price = p;
       deltaPrice = dp;
       upper = upp;
   }

    public String getDeltaPrice() {
        return deltaPrice;
    }

    public String getPrice() {
        return price;
    }

    public String getSigla() {
        return sigla;
    }

    public String getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }


    public boolean isUpper() {
        return upper;
    }
}
