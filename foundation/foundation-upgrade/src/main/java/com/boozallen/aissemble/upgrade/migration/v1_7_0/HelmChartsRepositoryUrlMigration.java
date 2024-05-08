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

import com.boozallen.aissemble.upgrade.migration.AbstractHelmRepositoryMigration;

/**
 * Baton migration used to migrate the Chart.yaml within a project's deploy folder
 * to use the new Helm chart repository URL
 * Example: https://old-helm-repository-url.com/ to oci://ghcr.io/boozallen
 */

public class HelmChartsRepositoryUrlMigration extends AbstractHelmRepositoryMigration {
    private static final String NEW_HELM_REPO_URL = "oci://ghcr.io/boozallen";
    @Override
    public String getReplacementRegex() {
        return getOldHelmRepositoryUrl();
    }

    @Override
    public String getReplacementText() {
        return NEW_HELM_REPO_URL;
    }
}
