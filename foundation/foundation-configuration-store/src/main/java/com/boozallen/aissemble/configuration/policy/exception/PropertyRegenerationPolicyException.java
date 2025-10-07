package com.boozallen.aissemble.configuration.policy.exception;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Configuration::Store
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
 * Class to represent exceptions during Property Regeneration Policy execution.
 */
public class PropertyRegenerationPolicyException extends RuntimeException {
    
    public PropertyRegenerationPolicyException() {
        super();
    }

    public PropertyRegenerationPolicyException(String message, Throwable cause) {
        super(message, cause);
    }

    public PropertyRegenerationPolicyException(String message) {
        super(message);
    }

    public PropertyRegenerationPolicyException(Throwable cause) {
        super(cause);
    }
}
