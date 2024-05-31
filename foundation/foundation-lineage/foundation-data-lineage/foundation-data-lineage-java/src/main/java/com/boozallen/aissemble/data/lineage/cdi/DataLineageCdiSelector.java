package com.boozallen.aissemble.data.lineage.cdi;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Data Lineage Java
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aissemble.core.cdi.CdiContext;
import com.boozallen.aissemble.data.lineage.config.ConfigUtil;

public final class DataLineageCdiSelector {
    private DataLineageCdiSelector() {}

    private static ConfigUtil config() {
        return ConfigUtil.getInstance();
    }

    public static CdiContext getDataLineageCdiContext() {
        if ("true".equalsIgnoreCase(config().shouldEmitToConsole())) {
            return new DataLineageConsoleEmissionCdiContext();
        } else {
            return new DataLineageCdiContext();
        }
    }
}
