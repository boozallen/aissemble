package com.boozallen.aissemble.security.exception;

/*-
 * #%L
 * aiSSEMBLE::Extensions::Security::Authzforce::Extensions::Security::Authzforce
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
 * An exception to denote cases from which there is no ability to recover.
 */
public class UnrecoverableException extends AissembleSecurityException {

    private static final long serialVersionUID = -4923273764539689604L;

    /**
     * {@inheritDoc}
     */
    public UnrecoverableException() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    public UnrecoverableException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * {@inheritDoc}
     */
    public UnrecoverableException(String message) {
        super(message);
    }

    /**
     * {@inheritDoc}
     */
    public UnrecoverableException(Throwable cause) {
        super(cause);
    }
}
