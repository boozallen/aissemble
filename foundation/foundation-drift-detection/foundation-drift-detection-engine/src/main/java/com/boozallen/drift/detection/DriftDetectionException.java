package com.boozallen.drift.detection;

/*-
 * #%L
 * Drift Detection::Core
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

public class DriftDetectionException extends RuntimeException {

    private static final long serialVersionUID = 2317306686653948993L;

    public DriftDetectionException() {
        super();
    }

    public DriftDetectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public DriftDetectionException(String message) {
        super(message);
    }

    public DriftDetectionException(Throwable cause) {
        super(cause);
    }
}
