package com.boozallen.aissemble.maven.enforcer.helper;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Maven::Enforcer
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
 * Specific runtime exception used to indicate that this came from the shell execution lib.  Other javadoc not
 * overridden as this is not needed unless adding to the original javadoc.
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
