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

import com.boozallen.aissemble.upgrade.migration.v1_10_0.AlertingCdiMigration;
import org.apache.maven.project.MavenProject;

public class TestAlertingCdiMigration extends AlertingCdiMigration {
    private final MavenProject projectOverride;

    public TestAlertingCdiMigration(MavenProject projectOverride) {
        this.projectOverride = projectOverride;
    }

    @Override
    public MavenProject getMavenProject() {
        return projectOverride;
    }
}
