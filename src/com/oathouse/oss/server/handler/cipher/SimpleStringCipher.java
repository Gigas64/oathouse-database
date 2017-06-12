/*
 * @(#)SimpleStringCipher.java
 *
 * Copyright:	Copyright (c) 2012
 * Company:		Oathouse.com Ltd
 */
package com.oathouse.oss.server.handler.cipher;

import com.oathouse.oss.server.OssProperties;
import com.oathouse.oss.storage.exceptions.PersistenceException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

/**
 * The {@code SimpleStringCipher} Class
 *
 * @author Darryl Oatridge
 * @version 1.00 Nov 6, 2012
 */
public class SimpleStringCipher {

    private final static byte[] linebreak = {}; // Remove Base64 encoder default linebreak
    private final static String secret; //must be 16 characters
    private final static SecretKey key;
    private final static Cipher cipher;
    private final static Base64 coder;

    static {
        try {
            secret = StringUtils.substring(OssProperties.getInstance().getCipherKey() + StringUtils.substring(OssProperties.getInstance().getAuthority(), -4) + "9Uj2wS", -16);
            key = new SecretKeySpec(secret.getBytes(), "AES");
            cipher = Cipher.getInstance("AES/ECB/PKCS5Padding", "SunJCE");
            coder = new Base64(32, linebreak, true);
        } catch(NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException ex) {
            throw new ExceptionInInitializerError("Unable to create String Cipher");
        }
    }

    public static synchronized String encrypt(String plainText) throws PersistenceException {
        byte[] cipherText;
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key);
            cipherText = cipher.doFinal(plainText.getBytes());
        } catch( InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
            throw new PersistenceException("Unable to encrypt ciphered data: " +  ex.getMessage());
        }
        return new String(coder.encode(cipherText));
    }

    public static synchronized String decrypt(String codedText) throws PersistenceException {
        byte[] encypted = coder.decode(codedText.getBytes());
        byte[] decrypted;
        try {
            cipher.init(Cipher.DECRYPT_MODE, key);
            decrypted = cipher.doFinal(encypted);
        } catch( InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
            throw new PersistenceException("Decryption of the ObjectBean failed. The cipher key might have changed");
        }
        return new String(decrypted);
    }

    private SimpleStringCipher() {
    }
}
