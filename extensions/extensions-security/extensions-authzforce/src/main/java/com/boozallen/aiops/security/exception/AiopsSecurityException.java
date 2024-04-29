package com.boozallen.aiops.security.exception;

/*-
 * #%L
 * aiSSEMBLE::Extensions::Security::Authzforce::Extensions::Security::Authzforce
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

/**
 * Exception for AIOps security.
 */
public class AiopsSecurityException extends RuntimeException {

    private static final long serialVersionUID = -6355403160236679418L;

    public AiopsSecurityException() {
        super();
    }

    public AiopsSecurityException(String message, Throwable cause) {
        super(message, cause);
    }

    public AiopsSecurityException(String message) {
        super(message);
    }

    public AiopsSecurityException(Throwable cause) {
        super(cause);
    }

}
