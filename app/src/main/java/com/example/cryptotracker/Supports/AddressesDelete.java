package com.example.cryptotracker.Supports;

public class AddressesDelete {
    private String Chain;
    private String Address;


    // create constructor to set the values for all the parameters of the each single view
    public AddressesDelete( String chain, String address) {
        Chain = chain;
        Address = address;
    }
    // getter method for returning the ID of the TextView 1
    public String getChain() {
        return Chain;
    }
    public String getAddress() {
        return Address;
    }


}
