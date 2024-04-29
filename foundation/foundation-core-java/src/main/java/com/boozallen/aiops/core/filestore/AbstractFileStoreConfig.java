package com.boozallen.aiops.core.filestore;

/*-
 * #%L
 * AIOps Foundation::AIOps Core
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import java.util.Properties;

public abstract class AbstractFileStoreConfig implements FileStoreConfig {

    private final String name;

    public AbstractFileStoreConfig(final String name) {
        this.name = name;
    }

    @Override
    public abstract String getProvider();

    @Override
    public abstract String getAccessKeyId();

    @Override
    public abstract String getSecretAccessKey();

    @Override
    public abstract Properties getOverrides();

    public String getName() {
        return this.name;
    }
}
