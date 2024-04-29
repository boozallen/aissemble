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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.boozallen.aissemble.data.encryption.exception.AiopsEncryptException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Utility class for common hashing operations.
 */
public final class AiopsHash {

    private static final Logger logger = LoggerFactory.getLogger(AiopsHash.class);

    /**
     * This method creates a hash for the given input using SHA-1
     * @param valueToHash the string to hash
     * @param salt a salt used to confound potential attacks
     * @return the hashed value
     */
    public static String getSHA1Hash(String valueToHash, byte[] salt) {
        String hashedValue = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(salt);
            byte[] bytes = md.digest(valueToHash.getBytes());
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++)
            {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            hashedValue = sb.toString();
        }
        catch (NoSuchAlgorithmException e)
        {
            logger.error("Unable to create a hash", e);

            throw new AiopsEncryptException("Unable to create a hash", e);
        }
        return hashedValue;
    }

    /**
     * A simple random salt generator
     * @return a salt value
     * @throws NoSuchAlgorithmException
     */
    public static byte[] getSalt() throws NoSuchAlgorithmException {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return salt;
    }
}

