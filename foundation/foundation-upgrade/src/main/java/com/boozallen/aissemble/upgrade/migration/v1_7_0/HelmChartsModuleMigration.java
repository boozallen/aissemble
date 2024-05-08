package com.boozallen.aissemble.upgrade.migration.v1_7_0;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Upgrade
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aissemble.upgrade.migration.AbstractHelmNameMigration;

import java.io.File;

/**
 * Baton migration used to migrate the Chart.yaml and values*.yaml files within a project's deploy folder
 * to use new Helm module names
 * Example: extensions-helm-kafka to aissemble-kafka-chart
 */
public class HelmChartsModuleMigration extends AbstractHelmNameMigration {
    private static final String moduleNameReplaceRegex = "extensions-helm-([^\\/\\n:]+)";
    private static final String AISSEMBLE_PREFIX = "aissemble-";
    private static final String CHART_POSTFIX = "-chart";

    @Override
    public String getReplacementRegex(File file) {
        return moduleNameReplaceRegex;
    }

    @Override
    public String getReplacementText(File file) {
        return AISSEMBLE_PREFIX.concat(FIRST_REGEX_GROUPING + CHART_POSTFIX);
    }
}
