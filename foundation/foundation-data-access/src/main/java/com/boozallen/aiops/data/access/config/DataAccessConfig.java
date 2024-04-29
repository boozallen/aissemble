package com.boozallen.aiops.data.access.config;

/*-
 * #%L
 * AIOps Foundation::AIOps Data Access
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import org.aeonbits.owner.KrauseningConfig;

@KrauseningConfig.KrauseningSources("data-access.properties")
public interface DataAccessConfig extends KrauseningConfig {

    /**
     * Query size limit
     * 
     * @return the default query size limit
     */
    @Key("default.query.size")
    @DefaultValue("500")
    Integer defaultQuerySize();

}
