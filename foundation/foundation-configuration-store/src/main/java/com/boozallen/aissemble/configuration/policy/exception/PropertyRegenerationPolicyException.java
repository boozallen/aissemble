package com.boozallen.aissemble.configuration.policy.exception;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Configuration::Store
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

/**
 * Class to represent exceptions during Property Regeneration Policy execution.
 */
public class PropertyRegenerationPolicyException extends RuntimeException {
    
    public PropertyRegenerationPolicyException() {
        super();
    }

    public PropertyRegenerationPolicyException(String message, Throwable cause) {
        super(message, cause);
    }

    public PropertyRegenerationPolicyException(String message) {
        super(message);
    }

    public PropertyRegenerationPolicyException(Throwable cause) {
        super(cause);
    }
}
