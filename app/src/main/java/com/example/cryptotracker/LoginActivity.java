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

import com.example.cryptotracker.Connections.RTFirebase;
import com.example.cryptotracker.Supports.Encrypt;
import com.example.cryptotracker.Supports.SharedPreferencesManager;
import com.example.cryptotracker.Supports.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextEmail;
    private EditText editTextPassword;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_login);

        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        Button buttonLogin = findViewById(R.id.login);
        TextView textViewRegister = findViewById(R.id.register);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleLogin();
            }
        });

        textViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleRegistration();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            currentUser.reload();
        }
    }

    private void handleLogin() {
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Inserisci email e password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (performLogin(email, password)) {
//            Log.d("SHAREDPREF", "msg: " + SharedPreferencesManager.getString(getApplicationContext(), "name", "Pippo"));
            Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
            LoginActivity.this.startActivity(myIntent);
        } else {
            Toast.makeText(this, "Email o password errati", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleRegistration() {
        Intent myIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        LoginActivity.this.startActivity(myIntent);
    }

    private boolean performLogin(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            RTFirebase rtFirebase = new RTFirebase();
                            rtFirebase.getDatabaseUserByUserId(Encrypt.hash(email), new User.UserCallback() {
                                @Override
                                public void onSuccess(User user) {
                                    SharedPreferencesManager.saveAccessInformation(getApplicationContext(), user.name, user.surname, email);
                                }

                                @Override
                                public void onFailure(String errorMessage) {
                                    Log.e("Callback", "Error: " + errorMessage);
                                    throw new RuntimeException();
                                }
                            });
                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        return mAuth.getCurrentUser() != null;
    }

}