package application.utils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Base64;

public class EncryptionHandler {
    private static final String STORE_PASSWORD = EnvHandler.get("KEYSTORE_PASS");
    private static final String KEYSTORE_FILE = "keystore.jceks";
    private static final String AES = "AES";
    private static final String AES_GCM_NOPADDING = "AES/GCM/NoPadding";
    private static final int GCM_TAG_LENGTH = 128; // bits
    private static final int IV_LENGTH = 12; // bytes

    public static void generateAndStoreKey(String keyStoreFile, String storePassword) {
        try {
            if (Files.exists(Paths.get(keyStoreFile))) {
                System.out.println("KeyStore already exists. Skipping key generation.");
                return;
            }

            KeyStore keyStore = KeyStore.getInstance("JCEKS");
            keyStore.load(null, null);

            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128);
            SecretKey secretKey = keyGen.generateKey();

            KeyStore.SecretKeyEntry entry = new KeyStore.SecretKeyEntry(secretKey);
            KeyStore.ProtectionParameter protectionParam = new KeyStore.PasswordProtection(storePassword.toCharArray());

            keyStore.setEntry("aesKey", entry, protectionParam);

            try (FileOutputStream fos = new FileOutputStream(keyStoreFile)) {
                keyStore.store(fos, storePassword.toCharArray());
                System.out.println("Key stored in: " + keyStoreFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static SecretKey getKey() throws Exception {
        if (STORE_PASSWORD == null || STORE_PASSWORD.isBlank()) {
            throw new IllegalStateException("KEYSTORE_PASS is not set. Please configure it in your .env file or environment.");
        }
        KeyStore keyStore = KeyStore.getInstance("JCEKS");
        keyStore.load(Files.newInputStream(Paths.get(KEYSTORE_FILE)), STORE_PASSWORD.toCharArray());

        KeyStore.ProtectionParameter protectionParam = new KeyStore.PasswordProtection(STORE_PASSWORD.toCharArray());
        KeyStore.SecretKeyEntry entry = (KeyStore.SecretKeyEntry) keyStore.getEntry("aesKey", protectionParam);

        return entry.getSecretKey();
    }


    public static String encrypt(String plaintext) throws Exception {
        Cipher cipher = Cipher.getInstance(AES_GCM_NOPADDING);
        byte[] iv = new byte[IV_LENGTH];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.ENCRYPT_MODE, getKey(), spec);

        byte[] encrypted = cipher.doFinal(plaintext.getBytes("UTF-8"));

        byte[] encryptedWithIv = new byte[IV_LENGTH + encrypted.length];
        System.arraycopy(iv, 0, encryptedWithIv, 0, IV_LENGTH);
        System.arraycopy(encrypted, 0, encryptedWithIv, IV_LENGTH, encrypted.length);

        return Base64.getEncoder().encodeToString(encryptedWithIv);
    }

    public static String decrypt(String encryptedBase64) throws Exception {
        byte[] encryptedWithIv = Base64.getDecoder().decode(encryptedBase64);
        byte[] iv = new byte[IV_LENGTH];
        byte[] encrypted = new byte[encryptedWithIv.length - IV_LENGTH];

        System.arraycopy(encryptedWithIv, 0, iv, 0, IV_LENGTH);
        System.arraycopy(encryptedWithIv, IV_LENGTH, encrypted, 0, encrypted.length);

        Cipher cipher = Cipher.getInstance(AES_GCM_NOPADDING);
        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.DECRYPT_MODE, getKey(), spec);

        byte[] decrypted = cipher.doFinal(encrypted);
        return new String(decrypted, "UTF-8");
    }

}
