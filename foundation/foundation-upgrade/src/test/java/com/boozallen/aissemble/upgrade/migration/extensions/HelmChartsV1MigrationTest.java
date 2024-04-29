package com.boozallen.aissemble.upgrade.migration.extensions;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Upgrade
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aissemble.upgrade.migration.HelmChartsV1Migration;

public class HelmChartsV1MigrationTest extends HelmChartsV1Migration {
    @Override
    protected String getAissembleVersion() {
        return "1.5.0-SNAPSHOT";
    }

    public String getCurrentAissembleVersion() {
        return getAissembleVersion();
    }
    public String getOutdatedAissembleVersion() {
        return "1.4.0-SNAPSHOT";

    }

    public String getNewerAissembleVersion() {
        return "1.6.0-SNAPSHOT";
    }
}
