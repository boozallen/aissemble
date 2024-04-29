package com.boozallen.aissemble.data.encryption.policy;

/*-
 * #%L
 * aiSSEMBLE Data Encryption::Policy::Java
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aissemble.data.encryption.policy.config.EncryptAlgorithm;
import com.boozallen.aissemble.core.policy.configuration.policy.DefaultPolicy;

import java.util.List;

public class DefaultEncryptionPolicy extends DefaultPolicy implements EncryptionPolicy {
    private List<String> encryptFields;

    private String encryptPhase;

    private EncryptAlgorithm encryptAlgorithm;

    public DefaultEncryptionPolicy() {
        super();
    }

    public DefaultEncryptionPolicy(String identifier) {
        super(identifier);
    }

    @Override
    public List<String> getEncryptFields() {
        return encryptFields;
    }

    @Override
    public String getEncryptPhase() {
        return encryptPhase;
    }

    @Override
    public EncryptAlgorithm getEncryptAlgorithm() {
        return encryptAlgorithm;
    }

    public void setEncryptFields(List<String> encryptFields) {
        this.encryptFields = encryptFields;
    }

    public void setEncryptPhase(String encryptPhase) {
        this.encryptPhase = encryptPhase;
    }

    public void setEncryptAlgorithm(EncryptAlgorithm encryptAlgorithm) {
        this.encryptAlgorithm = encryptAlgorithm;
    }
}
