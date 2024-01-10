package com.example.cryptotracker;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cryptotracker.Supports.SharedPreferencesManager;
import com.example.cryptotracker.Supports.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_register);

        Button signUpButton = findViewById(R.id.signUpButton);
        TextView textViewLogin = findViewById(R.id.textViewLogin);

        textViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleLogin();
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    saveRegisterInformation();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void handleLogin() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    private void saveRegisterInformation() throws Exception {
        String name = getEditTextValue(R.id.nomeText);
        String surname = getEditTextValue(R.id.cognomeText);
        String email = getEditTextValue(R.id.emailText);
        String password = getEditTextValue(R.id.passwordText);
        String confirmPassword = getEditTextValue(R.id.confirmPasswordText);

        if (!validateInput(name, surname, email, password, confirmPassword)) {
            return;
        }

        User user = new User(email, name, surname);

        user.writeNewUser(email, name, surname, email);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            SharedPreferencesManager.saveAccessInformation(getApplicationContext(), name, surname, email);
                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private String getEditTextValue(int editTextId) {
        return ((EditText) findViewById(editTextId)).getText().toString().trim();
    }

    private boolean validateInput(String name, String surname, String email, String password, String confirmPassword) {
        if (name.isEmpty() || surname.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (password.length() < 8) {
            Toast.makeText(this, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show();
            return false;
        }

//        TODO: SCOMMENTARE
//        if (!password.matches(".*[A-Z].*")) {
//            Toast.makeText(this, "Password must contain at least one uppercase letter", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//
//        if (!password.matches(".*\\d.*")) {
//            Toast.makeText(this, "Password must contain at least one digit", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//
//        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\",.<>/?].*")) {
//            Toast.makeText(this, "Password must contain at least one special character", Toast.LENGTH_SHORT).show();
//            return false;
//        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "The two passwords do not match each other", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Add more validation as needed

        return true;
    }
}
