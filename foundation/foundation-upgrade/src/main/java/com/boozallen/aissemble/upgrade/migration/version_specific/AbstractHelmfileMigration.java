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

import org.apache.commons.lang3.StringUtils;

import com.boozallen.aissemble.upgrade.migration.AbstractAissembleMigration;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.technologybrewery.baton.util.FileUtils.readAllFileLines;

public abstract class AbstractHelmfileMigration extends AbstractAissembleMigration {

    public static final String HELMFILE_MIGRATION_ENABLE_KEY = "aissemble.enable.helmfile.migration";

    @Override
    protected boolean shouldExecuteOnFile(File file) {
        return isHelmfileMigrationActive() && shouldExecuteOnFileImpl(file);
    }

    protected abstract boolean shouldExecuteOnFileImpl(File file);

    public boolean isHelmfileMigrationActive() {
        return StringUtils.equalsIgnoreCase("true", System.getProperty(HELMFILE_MIGRATION_ENABLE_KEY));
    }

    protected boolean isArgoCDApplicationTemplate(File file) throws IOException {
        List<String> content = readAllFileLines(file);
        boolean isApplication = false;
        boolean isArgoApiVersion = false;

        for (String line : content) {
            if (!isArgoApiVersion) {
                isArgoApiVersion = line.trim().matches("apiVersion:\s*argoproj\\.io\\/v1alpha1");
            }
            if (!isApplication) {
                isApplication = line.trim().matches("kind:\s*Application");
            }
            if (isApplication && isArgoApiVersion) {
                return true;
            }
        }
        return false;
    }

}
