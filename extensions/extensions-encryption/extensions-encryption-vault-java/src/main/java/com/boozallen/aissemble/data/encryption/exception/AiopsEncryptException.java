package com.boozallen.aissemble.data.encryption.exception;

/*-
 * #%L
 * aiSSEMBLE Data Encryption::Encryption (Java)
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

/**
 * Exception for AIOps encryption.
 */
public class AiopsEncryptException extends RuntimeException {

    private static final long serialVersionUID = -6355403160236679418L;

    public AiopsEncryptException() {
        super();
    }

    public AiopsEncryptException(String message, Throwable cause) {
        super(message, cause);
    }

    public AiopsEncryptException(String message) {
        super(message);
    }

    public AiopsEncryptException(Throwable cause) {
        super(cause);
    }

}
