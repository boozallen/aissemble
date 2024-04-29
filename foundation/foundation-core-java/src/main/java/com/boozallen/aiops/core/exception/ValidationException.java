package com.boozallen.aiops.core.exception;

/*-
 * #%L
 * AIOps Foundation::AIOps Core
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

/**
 * Exception for validation issues encountered with AIOps.
 */
public class ValidationException extends AIOpsException {

    private static final long serialVersionUID = 4877801422313174893L;

    /**
     * {@inheritDoc}
     */
    public ValidationException() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * {@inheritDoc}
     */
    public ValidationException(String message) {
        super(message);
    }

    /**
     * {@inheritDoc}
     */
    public ValidationException(Throwable cause) {
        super(cause);
    }

}
