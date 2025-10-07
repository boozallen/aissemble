package com.boozallen.aissemble.core.exception;

/*-
 * #%L
 * aiSSEMBLE Foundation::aiSSEMBLE Core
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
