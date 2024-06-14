package com.boozallen.aissemble.configuration.dao;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Configuration::Store
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
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
