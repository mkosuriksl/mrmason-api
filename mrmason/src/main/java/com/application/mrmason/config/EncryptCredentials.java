package com.application.mrmason.config;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.util.Base64;

public class EncryptCredentials {
    private static final String ALGORITHM = "AES";
    private static final String ENCRYPTION_KEY = "MySuperSecretKey123"; // Ideally should be loaded from config

    // Method to generate a valid AES key of 16 bytes from the original key
    private static SecretKeySpec generateKey(String key) throws Exception {
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] keyBytes = sha.digest(key.getBytes("UTF-8"));
        // Use only the first 16 bytes for AES-128
        return new SecretKeySpec(keyBytes, 0, 16, ALGORITHM);
    }

    public static String encrypt(String plainText) {
        try {
            SecretKeySpec secretKey = generateKey(ENCRYPTION_KEY); // Generate the key
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encrypted = cipher.doFinal(plainText.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("Error while encrypting: " + e.getMessage(), e);
        }
    }

    public static String decrypt(String encryptedText) {
        try {
            SecretKeySpec secretKey = generateKey(ENCRYPTION_KEY); // Generate the key
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decodedBytes = Base64.getDecoder().decode(encryptedText);
            byte[] decrypted = cipher.doFinal(decodedBytes);
            return new String(decrypted);
        } catch (Exception e) {
            throw new RuntimeException("Error while decrypting: " + e.getMessage(), e);
        }
    }
}
