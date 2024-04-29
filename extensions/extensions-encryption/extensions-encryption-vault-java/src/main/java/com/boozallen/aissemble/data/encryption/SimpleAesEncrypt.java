package com.boozallen.aissemble.data.encryption;

/*-
 * #%L
 * aiSSEMBLE Data Encryption::Encryption (Java)
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aissemble.data.encryption.config.DataEncryptionConfiguration;
import com.boozallen.aissemble.data.encryption.exception.AiopsEncryptException;

import org.aeonbits.owner.KrauseningConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Utility class for common encryption operations using the AES algorithm.
 */
public class SimpleAesEncrypt implements AiopsEncrypt {

    private static final Logger logger = LoggerFactory.getLogger(SimpleAesEncrypt.class);
    private static final DataEncryptionConfiguration config = KrauseningConfigFactory.create(DataEncryptionConfiguration.class);

    /**
     * This method encrypts a string using the AES algorithm with PKCS5 padding.  The secret key spec is stored in
     * the encrypt.properties file in the encrypt.aes.secret.key.spec property
     * @param valueToEncrypt the string to encrypt
     * @return the encrypted value
     */
    public String encryptValue(String valueToEncrypt) {
        String result = null;
        try {
            byte[] iv = new byte[16];

            SecretKey aesKey = new SecretKeySpec(config.getEncryptAesSecretKeySpec().getBytes(StandardCharsets.UTF_8), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, aesKey, new IvParameterSpec(iv));
            result = new String(Base64.getEncoder().encode(cipher.doFinal(valueToEncrypt.getBytes())));
        } catch(NoSuchPaddingException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException |
                InvalidKeyException | InvalidAlgorithmParameterException e) {
            throw new AiopsEncryptException("Unable to encrypt with AES", e);
        }

        return result;
    }

    /**
     * This method decrypts a string using the AES algorithm with PKCS5 padding.  The secret key spec is stored in
     * the encrypt.properties file in the encrypt.aes.secret.key.spec property
     * @param valueTodecrypt the string to decrypt
     * @return the decrypted value
     */
    public String decryptValue(String valueTodecrypt) {
        String result = null;
        try {
            byte[] iv = new byte[16];
            SecretKey aesKey = new SecretKeySpec(config.getEncryptAesSecretKeySpec().getBytes(StandardCharsets.UTF_8), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");

            cipher.init(Cipher.DECRYPT_MODE, aesKey, new IvParameterSpec(iv));

            result = new String(cipher.doFinal(Base64.getDecoder().decode(valueTodecrypt)));
        } catch(NoSuchPaddingException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException |
                InvalidKeyException | InvalidAlgorithmParameterException e) {
            throw new AiopsEncryptException("Unable to encrypt with AES", e);
        }

        return result;
    }
}

