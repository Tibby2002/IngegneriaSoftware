package com.example.cryptotracker.Connections;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RTFirebase {

    private DatabaseReference mDatabase;

    public RTFirebase(){
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public DatabaseReference getDatabaseReference() {
        return mDatabase;
    }
}
