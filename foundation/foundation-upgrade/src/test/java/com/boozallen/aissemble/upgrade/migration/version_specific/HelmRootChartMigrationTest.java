package com.boozallen.aissemble.upgrade.migration.version_specific;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Upgrade
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

/**
 * Test class to override the desired aissemble version
 */
public class HelmRootChartMigrationTest extends HelmRootChartMigration {

    // Added so the test doesn't require a pom.xml to parse through to get the version
    @Override
    public String getAissembleVersion() {
        return "1.12.1";
    }
}
