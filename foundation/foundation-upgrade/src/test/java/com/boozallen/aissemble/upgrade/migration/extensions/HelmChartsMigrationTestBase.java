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

import com.boozallen.aissemble.upgrade.migration.HelmChartsV2Migration;

public abstract class HelmChartsMigrationTestBase extends HelmChartsV2Migration {

    public String getCurrentAissembleVersion() {
        return getAissembleVersion();
    }
    public abstract String getOutdatedAissembleVersion();

    public abstract String getNewerAissembleVersion();

}
