package com.boozallen.aissemble.security.authorization;

/*-
 * #%L
 * aiSSEMBLE::Extensions::Security::Authzforce::Extensions::Security::Authzforce
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.FileInputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.X509Certificate;

import com.boozallen.aissemble.security.config.SecurityConfiguration;
import com.boozallen.aissemble.security.exception.AissembleSecurityException;
import org.aeonbits.owner.KrauseningConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class to load and hold keystore information for aissemble security.
 */
public class AissembleKeyStore {

    private static final Logger logger = LoggerFactory.getLogger(AissembleKeyStore.class);
    private static final SecurityConfiguration config = KrauseningConfigFactory.create(SecurityConfiguration.class);

    private static final String KEY_ALIAS = config.getKeyAlias();
    private static final String KEYSTORE_LOCATION = config.getKeyStoreLocation();
    private static final String KEYSTORE_PASSWORD = config.getKeyStorePassword();
    private static final String KEYSTORE_TYPE = config.getKeyStoreType();

    private final X509Certificate certificate;
    private final Key signingKey;

    public AissembleKeyStore() {
        KeyStore keyStore = loadKeyStore();
        certificate = getCertificateFromKeyStore(keyStore);
        signingKey = getKeyFromKeyStore(keyStore);
    }

    public X509Certificate getCertificate() {
        return certificate;
    }

    public Key getSigningKey() {
        return signingKey;
    }

    private KeyStore loadKeyStore() {
        KeyStore keyStore = null;

        try {
            keyStore = KeyStore.getInstance(KEYSTORE_TYPE);
            keyStore.load(new FileInputStream(KEYSTORE_LOCATION), KEYSTORE_PASSWORD.toCharArray());
        } catch (Exception e) {
            logger.error("Error loading keystore", e);
            throw new AissembleSecurityException("Unable to load keystore!", e);
        }

        return keyStore;
    }

    private X509Certificate getCertificateFromKeyStore(KeyStore keyStore) {
        X509Certificate cert = null;

        try {
            cert = (X509Certificate) keyStore.getCertificate(KEY_ALIAS);
        } catch (KeyStoreException e) {
            logger.error("Error getting certificate from keystore", e);
            throw new AissembleSecurityException("Unable to get certificate from keystore!", e);
        }

        return cert;
    }

    private Key getKeyFromKeyStore(KeyStore keyStore) {
        Key key = null;

        try {
            key = keyStore.getKey(KEY_ALIAS, KEYSTORE_PASSWORD.toCharArray());
        } catch (UnrecoverableKeyException | KeyStoreException | NoSuchAlgorithmException e) {
            logger.error("Error getting key from keystore", e);
            throw new AissembleSecurityException("Unable to get key from keystore!", e);
        }

        return key;
    }

}
