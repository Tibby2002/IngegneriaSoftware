package com.example.cryptotracker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.cryptotracker.Connections.RTFirebase;
import com.example.cryptotracker.Supports.Encrypt;
import com.example.cryptotracker.Supports.SharedPreferencesManager;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;

public class ProfileFragment extends Fragment {
    private EditText nameText;
    private EditText surnameText;
    private EditText emailText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        String name = SharedPreferencesManager.getString(getContext(), "name", "Nome");
        String surname = SharedPreferencesManager.getString(getContext(), "surname", "Cognome");
        String email = SharedPreferencesManager.getString(getContext(), "email", "Email");

        nameText = rootView.findViewById(R.id.nameText);
        surnameText = rootView.findViewById(R.id.surnameText);
        emailText = rootView.findViewById(R.id.emailText);

        loadUserData(name, surname, email);

        Button changePasswordButton = rootView.findViewById(R.id.buttonChangePassword);
        Button saveButton = rootView.findViewById(R.id.buttonSave);
        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                performLogout();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndUpdateUserData(email, name, surname);
                Navigation.findNavController(v).navigate(R.id.nav_home);
            }
        });

        return rootView;
    }

    private void loadUserData(String name, String surname, String email) {
        nameText.setText(name);
        surnameText.setText(surname);
        emailText.setText(email);
    }

    private void checkAndUpdateUserData(String email, String name, String surname) {
        if (!name.equals(nameText.getText().toString()) || !surname.equals(surnameText.getText().toString())) {
            RTFirebase rtFirebase = new RTFirebase();
            if (!name.equals(nameText.getText().toString())) {
                SharedPreferencesManager.saveString(getContext(), "name", nameText.getText().toString());
                rtFirebase.updateUserData(Encrypt.hash(email), "name", nameText.getText().toString());
            }
            if (!surname.equals(surnameText.getText().toString())) {
                SharedPreferencesManager.saveString(getContext(), "surname", surnameText.getText().toString());
                rtFirebase.updateUserData(Encrypt.hash(email), "surname", surnameText.getText().toString());
            }
        }
    }
}
