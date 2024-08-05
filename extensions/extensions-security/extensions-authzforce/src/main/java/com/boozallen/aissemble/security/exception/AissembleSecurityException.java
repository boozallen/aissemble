package com.boozallen.aissemble.security.exception;

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
 * Exception for aissemble security.
 */
public class AissembleSecurityException extends RuntimeException {

    private static final long serialVersionUID = -6355403160236679418L;

    public AissembleSecurityException() {
        super();
    }

    public AissembleSecurityException(String message, Throwable cause) {
        super(message, cause);
    }

    public AissembleSecurityException(String message) {
        super(message);
    }

    public AissembleSecurityException(Throwable cause) {
        super(cause);
    }

}
