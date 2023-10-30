package com.example.cryptotracker.Supports;

import java.util.Collection;

public class User {
    public User(String n, String sur, String urlPhoto){
        wallets = null;
        name = n;
        surname = sur;
        this.urlPhoto = urlPhoto;
    }
    class Wallet{
        public String network;
        public String address;
        Wallet(String n, String a){
            network = n;
            address = a;
        }
    }
    Collection<Wallet> wallets;
    String name;
    String surname;

    String urlPhoto;

}
