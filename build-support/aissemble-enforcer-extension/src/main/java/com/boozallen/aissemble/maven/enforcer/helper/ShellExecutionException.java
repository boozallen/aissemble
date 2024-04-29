package com.boozallen.aissemble.maven.enforcer.helper;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Maven::Enforcer
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

/**
 * Specific runtime exception used to indicate that this came from the shell execution lib.  Other javadoc not
 * overridden as this is not needed unless adding to the original javadoc.
 */
public class ShellExecutionException extends RuntimeException {

    public ShellExecutionException() {
        super();
    }

    public ShellExecutionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ShellExecutionException(String message) {
        super(message);
    }

    public ShellExecutionException(Throwable cause) {
        super(cause);
    }
}
