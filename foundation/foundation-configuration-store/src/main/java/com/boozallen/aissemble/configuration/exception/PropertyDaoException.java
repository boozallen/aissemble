package com.boozallen.aissemble.configuration.exception;

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
