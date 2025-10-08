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
 * Exception for aissemble security.
 */
public class AissembleSecurityException extends RuntimeException {

    private static final long serialVersionUID = -6355403160236679418L;

    public AissembleSecurityException() {
        super();
    }

    public AissembleSecurityException(String message, Throwable cause) {
        super(message, cause);
    }

    public AissembleSecurityException(String message) {
        super(message);
    }

    public AissembleSecurityException(Throwable cause) {
        super(cause);
    }

}
