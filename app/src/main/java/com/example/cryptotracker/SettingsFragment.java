package com.example.cryptotracker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;

public class SettingsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Infla il layout del fragment
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        Button profileButton = rootView.findViewById(R.id.nav_profile);
        Button logoutButton = rootView.findViewById(R.id.action_settings_Fragment_to_logoutFragment);

        // Imposta il gestore per il clic del pulsante di logout
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Effettua il logout e reindirizza al fragment di logout
                FirebaseAuth.getInstance().signOut();
                Navigation.findNavController(v).navigate(R.id.nav_profile);
            }
        });

        // Imposta il gestore per il clic del pulsante del profilo
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Reindirizza al fragment del profilo (sostituisci con l'ID corretto)
                Navigation.findNavController(v).navigate(R.id.action_settings_Fragment_to_logoutFragment);
            }
        });

        return rootView;
    }
}
