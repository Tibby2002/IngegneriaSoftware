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
import androidx.fragment.app.Fragment;

import java.io.File;

public class Logout extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Infla il layout del fragment
        View rootView = inflater.inflate(R.layout.fragment_logout, container, false);

        // Trova il pulsante di logout nel layout del fragment
        Button logoutButton = rootView.findViewById(R.id.buttonLogout);

        // Imposta il gestore per il clic del pulsante di logout
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Esegui le operazioni di logout qui
                performLogout();
            }
        });

        return rootView;
    }

    private void performLogout() {
        // Aggiungi qui la logica per eseguire il logout
        // Ad esempio, reimposta le informazioni dell'utente o cancella la sessione
        // Elimina il file 'settings.txt'
        File path = requireContext().getFilesDir();
        File file = new File(path, "settings.txt");

        if (file.exists()) {
            boolean deleted = file.delete();
            if (deleted) {
                Log.d("Logout", "Settings deleted correctly");
            } else {
                throw new RuntimeException("Error deleting file");
            }
        }

        // Cambia il metodo di ottenere SharedPreferences in modo corretto
        requireActivity().getSharedPreferences("PREFERENCE", AppCompatActivity.MODE_PRIVATE)
                .edit()
                .putBoolean("isLogged", false)
                .apply();

        // Avvia l'activity di login dopo il logout
        Intent loginIntent = new Intent(requireContext(), Login.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        requireActivity().finish(); // Chiudi l'activity corrente
    }
}
