package de.tum.mitfahr.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by abhijith on 16/05/14.
 */
public class Crypto {

    private static final String SALT = "toj369sbz1f316sx";

    public static String sha512(String password) {
        String toHash = password + SALT;

        MessageDigest md = null;
        byte[] hash = null;
        try {
            md = MessageDigest.getInstance("SHA-512");
            hash = md.digest(toHash.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return convertToHex(hash);
    }


    private static String convertToHex(byte[] rawHash) {
        StringBuffer sb = new StringBuffer();
        for (byte rawByte : rawHash) {
            sb.append(Integer.toString((rawByte & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }


}
