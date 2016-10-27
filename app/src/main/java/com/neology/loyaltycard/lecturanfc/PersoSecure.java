package com.neology.loyaltycard.lecturanfc;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class PersoSecure {

    private static final String salt =
            "Se personaliza app para la lectura de datos por NFC FidelityCard Se personaliza app para la lectura de datos por NFC FidelityCard Se personaliza app para la lectura de datos por NFC FidelityCard Se personaliza app para la lectura de datos por NFC FidelityCard Se personaliza app para la lectura de datos por NFC FidelityCard Se personaliza app para la lectura de datos por NFC FidelityCard";
    private static final int iterations = 2000;
    private static final int keyLength = 128;

    public PersoSecure() {
        Security.insertProviderAt(new BouncyCastleProvider(), 1);
    }

    public static byte[] encrypt(byte[] raw, byte[] clear) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES", "BC");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(clear);
        return encrypted;
    }

    public static byte[] getSHA2(String rawtext) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        }
        digest.update(rawtext.getBytes());
        return digest.digest();
    }

    private static SecretKey generateKey(String passphrase) throws Exception {
        PBEKeySpec keySpec = new PBEKeySpec(passphrase.toCharArray(), salt.getBytes(), iterations, keyLength);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWITHSHAAND128BITAES-CBC-BC", "BC");
        return keyFactory.generateSecret(keySpec);
    }

    public byte[] decrypt(byte[] raw, byte[] encrypted) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES", "BC");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        byte[] decrypted = cipher.doFinal(encrypted);
        return decrypted;
    }

    public String bin2hex(byte[] data) {
        return String.format("%0" + (data.length * 2) + "X", new BigInteger(1,
                data));
    }

    public String getSHA2String(String rawtext) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        }
        digest.update(rawtext.getBytes());
        return bin2hex(digest.digest());
    }

    public byte[] getkey(String passphrase) throws Exception {
        SecretKey key = generateKey(passphrase);
        byte[] keybytes = key.getEncoded();
        //System.out.println("Key PBE ("+keybytes.length*8+"):" + bin2hex(keybytes));
        return keybytes;
    }

}
