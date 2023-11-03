package com.example.cryptotracker.Supports;

public class ModifyView {
    private String chain;
    private String address;

    public ModifyView( String chain, String address) {
       this.chain = chain;
       this.address = address;
    }
    public String getChain() {
        return chain;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String s){address = s;}

}
