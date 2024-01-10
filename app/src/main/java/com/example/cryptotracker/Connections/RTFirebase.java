package com.example.cryptotracker.Connections;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.cryptotracker.Supports.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.ExecutionException;

public class RTFirebase {

    private DatabaseReference mDatabase;

    public RTFirebase(){
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

}