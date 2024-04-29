package com.boozallen.data.transform;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Transform::Java
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

/**
 * {@link DataTransformException} class represents exceptions during data
 * transform execution.
 * 
 * @author Booz Allen Hamilton
 *
 */
public class DataTransformException extends RuntimeException {

    private static final long serialVersionUID = -5495688164631182830L;

    public DataTransformException() {
        super();
    }

    public DataTransformException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataTransformException(String message) {
        super(message);
    }

    public DataTransformException(Throwable cause) {
        super(cause);
    }

}
