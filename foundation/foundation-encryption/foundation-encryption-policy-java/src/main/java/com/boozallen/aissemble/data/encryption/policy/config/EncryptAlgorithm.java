package com.boozallen.aissemble.data.encryption.policy.config;

/*-
 * #%L
 * aiSSEMBLE Data Encryption::Policy::Java
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import java.util.HashMap;
import java.util.Map;

/**
 * {@link EncryptAlgorithm} enum is used to map the {@link EncryptAlgorithm} short
 * hand to the corresponding class. We can eventually use properties to do this
 * dynamically, but this sets up the basic structure of having a short hand
 * named mapped to a class name.
 * 
 * @author Booz Allen Hamilton
 *
 */
public enum EncryptAlgorithm {

    VAULT_ENCRYPT("VAULTENCRYPT", "com.boozallen.aissemble.data.encryption.VaultEncrypt"),
    AES_ENCRYPT("SIMPLEAESENCRYPT", "com.boozallen.aissemble.data.encryption.SimpleAesEncrypt");

    public final String shortHand;

    public final String className;

    private static final Map<String, String> SHORT_HAND_LOOKUP = new HashMap<>();

    static {
        for (EncryptAlgorithm e : values()) {
            SHORT_HAND_LOOKUP.put(e.shortHand, e.className);
        }
    }

    private EncryptAlgorithm(String shortHand, String className) {
        this.shortHand = shortHand;
        this.className = className;
    }

    public static boolean hasClassForShortHand(String shortHand) {
        return SHORT_HAND_LOOKUP.containsKey(shortHand.toUpperCase());
    }

    public static String getClassNameForShortHand(String shortHand) {
        return SHORT_HAND_LOOKUP.get(shortHand.toUpperCase());
    }
}
