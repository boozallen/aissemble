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
 * Generic runtime exception for issues encountered with AIOps.
 */
public class AIOpsException extends RuntimeException {

    private static final long serialVersionUID = 1937465015793832235L;

    /**
     * {@inheritDoc}
     */
    public AIOpsException() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    public AIOpsException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * {@inheritDoc}
     */
    public AIOpsException(String message) {
        super(message);
    }

    /**
     * {@inheritDoc}
     */
    public AIOpsException(Throwable cause) {
        super(cause);
    }

}
