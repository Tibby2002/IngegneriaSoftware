package com.example.cryptotracker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cryptotracker.Connections.MongoClientConnection;

import org.bson.Document;

public class Login extends AppCompatActivity {

    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonLogin;
    private TextView textViewRegister;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inizializza le view
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        textViewRegister = findViewById(R.id.textViewRegister);

        // Aggiungi un listener al pulsante di login
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Chiamata al metodo per gestire il login
                handleLogin();
            }
        });

        // Aggiungi un listener al testo per la registrazione
        textViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Chiamata al metodo per gestire la registrazione
                handleRegistration();
            }
        });
    }

    // Metodo per gestire il login
    private void handleLogin() {
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();

        // Esegui la validazione dei dati (puoi aggiungere ulteriori logiche qui)

        // Esempio: verifica se i campi sono vuoti
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Inserisci email e password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Esempio: esegui il login (sostituisci con la tua logica di autenticazione)
        if (performLogin(email, password)) {
            getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                    .putBoolean("isLogged", true).commit();
            Intent myIntent = new Intent(Login.this, MainActivity.class);
            Login.this.startActivity(myIntent);
        } else {
            // Login fallito, mostra un messaggio di errore
            Toast.makeText(this, "Email o password errati", Toast.LENGTH_SHORT).show();
        }
    }

    // Metodo per gestire la registrazione
    private void handleRegistration() {
        Intent myIntent = new Intent(Login.this, Register.class);
        Login.this.startActivity(myIntent);
    }

    // Esempio di metodo per eseguire il login (sostituisci con la tua logica di autenticazione)
    private boolean performLogin(String email, String password) {
//        MongoClientConnection dbConnection = new MongoClientConnection();
//        Document user = dbConnection.findUser(email);
//        Log.d("User", "kek" + user);
        return true;
    }
}
