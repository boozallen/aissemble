package com.boozallen.aissemble.pipeline.invocation.service.util.exec;

/*-
 * #%L
 * aiSSEMBLE::Extensions::Pipeline Invocation Service
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
 * Generic exception to use for shell execution failure.
 * TODO: Refactor to use Technology Brewery Commons once released: https://github.com/TechnologyBrewery/commons
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
