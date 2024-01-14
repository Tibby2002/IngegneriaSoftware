package com.example.cryptotracker.Connections;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.cryptotracker.Supports.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class RTFirebase {

    private DatabaseReference mDatabase;

    public RTFirebase() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public DatabaseReference getDatabaseReference() {
        return mDatabase;
    }

    public void getDatabaseUserByUserId(String userId, User.UserCallback callback) {
        DatabaseReference usersReference = mDatabase.child("users");

        usersReference.child(userId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                    callback.onFailure("Error getting data: " + task.getException().getMessage());
                } else {
                    DataSnapshot dataSnapshot = task.getResult();
                    if (dataSnapshot.exists()) {
                        // Retrieve data for the user
                        User user = dataSnapshot.getValue(User.class);
                        callback.onSuccess(user);
                    } else {
                        callback.onFailure("User with ID " + userId + " does not exist.");
                    }
                }
            }
        });
    }

    public void updateUserData(String userId, String key, String value) {
        DatabaseReference usersReference = mDatabase.child("users");
        usersReference.child(userId).child(key).setValue(value).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("UPDATE USER", "Update failed: " + e.getMessage());
                    }
                });
    }

    public void updateUserAddress(String userId, String key, String address) {
        DatabaseReference addressesReference = mDatabase.child("users").child(userId).child("addresses");
        addressesReference.child(key).setValue(address).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("UPDATE USER", "Update failed: " + e.getMessage());
                    }
                });
    }

    public void deleteUserAddress(String userId, String key) {
        DatabaseReference addressesReference = mDatabase.child("users").child(userId).child("addresses");;
        addressesReference.child(key).removeValue().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("DELETE ADDRESS", "Delete failed: " + task.getException());
            }
        });
    }

    public void getAllAddresses(String userId, AddressCallback callback) {
        DatabaseReference addressesReference = mDatabase.child("users").child(userId).child("addresses");

        addressesReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot addressSnapshot : dataSnapshot.getChildren()) {
                        String key = addressSnapshot.getKey();
                        String address = addressSnapshot.getValue(String.class);

                        callback.onAddressReceived(key, address);
                    }
                } else {
                    callback.onNoAddresses();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("GET ALL ADDRESSES", "Error getting addresses: " + databaseError.getMessage());
            }
        });
    }

    public interface AddressCallback {
        void onAddressReceived(String key, String address);
        void onNoAddresses();
    }

}