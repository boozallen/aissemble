package com.boozallen.aissemble.pipeline.invocation.service.util.exec;

/*-
 * #%L
 * aiSSEMBLE::Extensions::Pipeline Invocation Service
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

/**
 * Generic exception to use for shell execution failure.
 * TODO: Refactor to use Technology Brewery Commons once released: https://github.com/TechnologyBrewery/commons
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
