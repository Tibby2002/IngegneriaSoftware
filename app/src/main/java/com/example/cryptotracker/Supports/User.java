package com.example.cryptotracker.Supports;

import com.example.cryptotracker.Connections.RTFirebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Collection;

@IgnoreExtraProperties
public class User {

    public String userId;
    public String email;

    public String name;
    public String surname;


    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String email, String name, String surname) {
        this.email = email;
        this.name = name;
        this.surname = surname;
    }

    public void writeNewUser(String userId, String name, String surname, String email) {
        User user = new User(email, name, surname);
        RTFirebase rtFirebase = new RTFirebase();

        rtFirebase.getDatabaseReference().child("users").child(userId).setValue(user);

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
}
