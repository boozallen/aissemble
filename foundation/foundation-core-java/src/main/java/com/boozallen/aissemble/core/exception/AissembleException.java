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
 * Generic runtime exception for issues encountered with aiSSEMBLE.
 */
public class AissembleException extends RuntimeException {

    private static final long serialVersionUID = 1937465015793832235L;

    /**
     * {@inheritDoc}
     */
    public AissembleException() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    public AissembleException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * {@inheritDoc}
     */
    public AissembleException(String message) {
        super(message);
    }

    /**
     * {@inheritDoc}
     */
    public AissembleException(Throwable cause) {
        super(cause);
    }

}
