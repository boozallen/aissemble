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

import com.boozallen.aissemble.data.encryption.exception.AiopsEncryptException;

/**
 * Interface for all encryption operations.
 */
public interface AiopsEncrypt {
    /**
     * Encrypts a string
     * @param valueToEncrypt the string to encrypt
     * @return the encrypted string
     */
    public String encryptValue(String valueToEncrypt) throws AiopsEncryptException;

    /**
     * Decrypts a string
     * @param valueToDecrypt the string to decrypt
     * @return the decrypted string
     */
    public String decryptValue(String valueToDecrypt) throws AiopsEncryptException;
}

