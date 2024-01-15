package com.example.cryptotracker;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordFragment extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_change_password, container, false);

        Button saveButton = rootView.findViewById(R.id.buttonSave);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPasswordText = getEditTextValue(rootView, R.id.newPasswordText);
                String confirmNewPasswordText = getEditTextValue(rootView, R.id.confirmNewPasswordText);
                if (validatePassword(newPasswordText, confirmNewPasswordText)) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        user.updatePassword(newPasswordText)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Navigation.findNavController(v).navigate(R.id.nav_profile);
                                    } else {
                                        Toast.makeText(getContext(), "Errore di connessione. Riprova!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }

                }
            }
        });

        return rootView;
    }

    private String getEditTextValue(View rootView, int editTextId) {
        return ((EditText) rootView.findViewById(editTextId)).getText().toString().trim();
    }

    private boolean validatePassword(String password, String confirmPassword) {

        if (password.length() < 6) {
            Log.d("PASSWORD", password);
            Toast.makeText(getContext(), "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!password.matches(".*[A-Z].*")) {
            Toast.makeText(getContext(), "Password must contain at least one uppercase letter", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!password.matches(".*\\d.*")) {
            Toast.makeText(getContext(), "Password must contain at least one digit", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\",.<>/?].*")) {
            Toast.makeText(getContext(), "Password must contain at least one special character", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(getContext(), "The two passwords do not match each other", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

}
