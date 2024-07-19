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

public enum ConfigStoreDaoClass {
    vault("com.boozallen.aissemble.configuration.dao.VaultPropertyDao"),
    inMemory("com.boozallen.aissemble.configuration.dao.InMemoryPropertyDao"),
    krausening("com.boozallen.aissemble.configuration.dao.KrauseningPropertyDao");

    private final String value;

    ConfigStoreDaoClass(final String value){
        this.value = value;
    }

    public String getValue(){
        return this.value;
    }

}
