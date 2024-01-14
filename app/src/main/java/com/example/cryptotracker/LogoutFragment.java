package com.example.cryptotracker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava3.RxDataStore;
import androidx.fragment.app.Fragment;

import com.example.cryptotracker.Supports.DataStoreSingleton;
import com.example.cryptotracker.Supports.SharedPreferencesManager;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;

public class LogoutFragment extends Fragment {

    RxDataStore<Preferences> dataStoreRX;
    DataStoreSingleton dataStoreSingleton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_logout, container, false);

        dataStoreSingleton = DataStoreSingleton.getInstance();
        if (dataStoreSingleton.getDataStore() == null) {
            dataStoreRX = new RxPreferenceDataStoreBuilder(getContext(), "wallet").build();
        } else {
            dataStoreRX = dataStoreSingleton.getDataStore();
        }
        dataStoreSingleton.setDataStore(dataStoreRX);

        Button logoutButton = rootView.findViewById(R.id.buttonLogout);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogout();
            }
        });

        return rootView;
    }

    private void performLogout() {
        deleteAssets();
        SharedPreferencesManager.clearPreferences(getContext());
        FirebaseAuth.getInstance().signOut();
        navigateToLoginActivity();
    }

    private void deleteAssets() {
        dataStoreSingleton.clearDataStore();
    }

    private void navigateToLoginActivity() {
        Intent loginIntent = new Intent(requireContext(), LoginActivity.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        requireActivity().finish();
    }
}
