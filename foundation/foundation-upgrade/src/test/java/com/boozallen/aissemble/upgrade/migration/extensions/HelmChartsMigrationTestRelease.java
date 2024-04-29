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

public class HelmChartsMigrationTestRelease extends HelmChartsMigrationTestBase {

    @Override
    public String getAissembleVersion() {
        return "1.4.0";
    }

    @Override
    public String getOutdatedAissembleVersion() {
        return "1.3.0";
    }

    @Override
    public String getNewerAissembleVersion() {
        return "1.5.0";
    }
}
