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
import com.boozallen.aissemble.core.policy.configuration.policy.Policy;

import java.util.List;

public interface EncryptionPolicy extends Policy {
    public List<String> getEncryptFields();

    public String getEncryptPhase();

    public EncryptAlgorithm getEncryptAlgorithm();
}
