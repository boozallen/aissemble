package com.boozallen.aiops.mda.metamodel.element;

/*-
 * #%L
 * AIOps Foundation::AIOps MDA
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
 * Defines the contract for how provenance is configured.
 */
public interface Provenance extends AbstractEnabled {

    /**
     * Name of the resource being operated on.
     * @return the name of the resource
     */
    String getResource();

    /**
     * Name of the subject responsible for the action.
     * @return the name of the subject
     */
    String getSubject();

    /**
     * Name of the action being taken.
     * @return name of the action
     */
    String getAction();

}
