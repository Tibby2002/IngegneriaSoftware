package com.example.cryptotracker.Supports;

import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Encrypt {

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public static String hash(String toHash) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] encodedHash = digest.digest(toHash.getBytes());

            String hashString = bytesToHex(encodedHash);

            return hashString;
        } catch (NoSuchAlgorithmException e) {
            Log.d("HashException", "Excetpion: " + e);
            throw new RuntimeException();
        }
    }
}
