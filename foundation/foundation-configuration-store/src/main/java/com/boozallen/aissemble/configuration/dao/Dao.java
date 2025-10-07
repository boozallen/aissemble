package com.boozallen.aissemble.configuration.dao;

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

import java.util.Set;

/**
 * Dao is an interface to read/write T to the store
 */

public interface Dao<T,K> {

    boolean checkEmpty();

    /**
     * Read T from Store with given K key
     * @param K key
     * @return T
     */
    T read(K key);

    /**
     * Write given data T to the Store
     * @param data to be written to Store
     */
    void write(T data);


    /**
     * Write given Set of T to the Store
     * @param dataSet to be written to Store
     */
    void write(Set<T> dataSet);
}
