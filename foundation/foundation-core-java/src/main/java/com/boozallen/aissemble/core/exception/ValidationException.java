package com.boozallen.aissemble.core.exception;

/*-
 * #%L
 * aiSSEMBLE Foundation::aiSSEMBLE Core
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

/**
 * Exception for validation issues encountered with aiSSEMBLE.
 */
public class ValidationException extends AissembleException {

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
