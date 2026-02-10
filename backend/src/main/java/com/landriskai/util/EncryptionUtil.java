package com.landriskai.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Utility for encrypting sensitive data at rest (NFR-5)
 * Phone numbers and tokens are encrypted/hashed for security compliance
 */
public class EncryptionUtil {

    private static final String ALGORITHM = "AES";
    private static final int KEY_SIZE = 256;
    private static final SecureRandom RANDOM = new SecureRandom();
    private static SecretKey SECRET_KEY;

    static {
        try {
            // In production, load this from environment variables or Key Management Service
            // For now, generate a fixed key (should be externalised)
            KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
            keyGen.init(KEY_SIZE, RANDOM);
            SECRET_KEY = keyGen.generateKey();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to initialize encryption", e);
        }
    }

    /**
     * Encrypt sensitive data (e.g., phone numbers)
     * @param data - plaintext data to encrypt
     * @return encrypted data (Base64 encoded)
     */
    public static String encrypt(String data) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, SECRET_KEY);
            byte[] encryptedData = cipher.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(encryptedData);
        } catch (Exception e) {
            throw new RuntimeException("Encryption failed", e);
        }
    }

    /**
     * Decrypt encrypted data
     * @param encryptedData - encrypted data (Base64 encoded)
     * @return plaintext data
     */
    public static String decrypt(String encryptedData) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, SECRET_KEY);
            byte[] decodedData = Base64.getDecoder().decode(encryptedData);
            byte[] decryptedData = cipher.doFinal(decodedData);
            return new String(decryptedData);
        } catch (Exception e) {
            throw new RuntimeException("Decryption failed", e);
        }
    }

    /**
     * Hash data for secure lookups (one-way, cannot be reversed)
     * Used for phone lookup without decrypting all phone numbers
     * @param data - data to hash
     * @return SHA-256 hash (hex encoded)
     */
    public static String hash(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(data.getBytes());
            // Convert to hex string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hashing failed", e);
        }
    }

    /**
     * Generate a secure random token (for verification codes, report links, etc.)
     * @param length - length of token
     * @return random token (hex encoded)
     */
    public static String generateToken(int length) {
        byte[] randomBytes = new byte[length / 2];
        RANDOM.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    /**
     * Verify a token against a hash (e.g., verification code)
     * @param plainToken - plaintext token
     * @param hashedToken - expected hash
     * @return true if tokens match
     */
    public static boolean verifyToken(String plainToken, String hashedToken) {
        return hash(plainToken).equals(hashedToken);
    }
}
