package com.boozallen.aissemble.configuration.exception;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Configuration::Store
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

public class PropertyDaoException extends RuntimeException{
    public PropertyDaoException() {
        super();
    }

    public PropertyDaoException(String message, Throwable cause) {
        super(message, cause);
    }

    public PropertyDaoException(String message) {
        super(message);
    }

    public PropertyDaoException(Throwable cause) {
        super(cause);
    }
}
