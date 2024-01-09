package com.example.cryptotracker;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonLogin;
    private TextView textViewRegister;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_login);

        // Inizializza le view
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        buttonLogin = findViewById(R.id.login);
        textViewRegister = findViewById(R.id.register);

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

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            currentUser.reload();
        }
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
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
//                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(Login.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                        }
                    }
                });
        return mAuth.getCurrentUser() != null;
    }

}