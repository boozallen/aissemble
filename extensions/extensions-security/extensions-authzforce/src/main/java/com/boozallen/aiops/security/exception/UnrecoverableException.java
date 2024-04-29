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
 * An exception to denote cases from which there is no ability to recover.
 */
public class UnrecoverableException extends AiopsSecurityException {

    private static final long serialVersionUID = -4923273764539689604L;

    /**
     * {@inheritDoc}
     */
    public UnrecoverableException() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    public UnrecoverableException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * {@inheritDoc}
     */
    public UnrecoverableException(String message) {
        super(message);
    }

    /**
     * {@inheritDoc}
     */
    public UnrecoverableException(Throwable cause) {
        super(cause);
    }
}
