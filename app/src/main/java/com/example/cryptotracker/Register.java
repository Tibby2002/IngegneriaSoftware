package com.example.cryptotracker;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Register extends AppCompatActivity {
    private String tempPhoto = "Default-photo";
    ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                // Callback is invoked after the user selects a media item or closes the
                // photo picker.
                if (uri != null) {
                    Log.d("PhotoPicker", "Selected URI: " + uri);
                } else {
                    Log.d("PhotoPicker", "No media selected");
                }
            });

    private void SaveOnFile(String... s) throws IOException {
        File path = this.getFilesDir();
        File file = new File(path, "settings.txt");
        FileOutputStream stream = new FileOutputStream(file);
        try {
            for (String arg : s) {
                if (arg.equals("password")) {
                    // Codifica la password in Base64 prima di scriverla
                    byte[] passwordBytes = arg.getBytes();
                    String encodedPassword = Base64.encodeToString(passwordBytes, Base64.DEFAULT);
                    stream.write(encodedPassword.getBytes());
                } else {
                    stream.write(arg.getBytes());
                }
                stream.write("\n".getBytes());
            }
        } finally {
            stream.close();
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedhash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void SaveRegisterInformation(View view) {
        String name = ((EditText) findViewById(R.id.nomeText)).getText().toString();
        String surname = ((EditText) findViewById(R.id.cognomeText)).getText().toString();
        String email = ((EditText) findViewById(R.id.emailText)).getText().toString();
        String password = ((EditText) findViewById(R.id.passwordText)).getText().toString();

        // Hash password before saving
        Log.d("Password", "Not hashed " + password);
        String hashedPassword = hashPassword(password);
        Log.d("Password", "Hashed " + hashedPassword);

        try {
            SaveOnFile(name, surname, email, hashedPassword, tempPhoto);
        } catch (IOException e) {
            getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                    .putBoolean("isFirstRun", false).commit();
            Toast.makeText(this, "reinserire Tutti i dati a causa di un errore", Toast.LENGTH_LONG).show();
            recreate();
        }
        Intent myIntent = new Intent(Register.this, MainActivity.class);
        Register.this.startActivity(myIntent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Button btn = findViewById(R.id.button);
        btn.setOnClickListener(view -> pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build()));

    }

}