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

import com.boozallen.aissemble.upgrade.migration.AbstractAissembleMigration;
import org.technologybrewery.baton.BatonException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.technologybrewery.baton.util.FileUtils.readAllFileLines;

/**
 * This migration removes the ArgoCD application templates and the associated Chart.yaml, Chart.lock, and values*.yaml
 * files if the helmfile migration enable key is set to true.
 */
public class ArgoCDRemovalMigration extends AbstractAissembleMigration {

    private static final String HELMFILE_MIGRATION_ENABLE_KEY = "aissemble.enable.helmfile.migration";

    /**
     * Determines whether the migration should execute.
     * if the helmfile migration enable key is set and file is an ArgoCD application template, the migration will run.
     *
     * @param file yaml file to check
     * @return true if the migration should run
     */
    @Override
    protected boolean shouldExecuteOnFile(File file) {
        try {
            return isMigrationActivated() && isArgoCDApplicationTemplate(file);
        } catch (Exception e) {
            throw new BatonException("Could not check yaml file for ArgoCD removal: " + file.getPath(), e);
        }
    }

    /**
     * Perform the migration script to remove the ArgoCD yaml files if they exist
     *
     * @param file pom file to migrate
     * @return true if migration was successful
     */
    @Override
    protected boolean performMigration(File file) {
        return removeArgoCDFiles(file);
    }

    private boolean removeArgoCDFiles(File file) throws BatonException {
        try {
            // templates/ folder's parent directory
            Path parentDir = file.toPath().getParent().getParent();

            List<String> files = List.of("Chart.yaml", "Chart.lock", "values.yaml", "values-dev.yaml", "values-ci.yaml");
            for (String yaml: files) {
                // remove argo yaml files
                Files.deleteIfExists(parentDir.resolve(yaml));
            }
            return file.delete();
        } catch (Exception e) {
            throw new BatonException("Could not remove ArgoCD yaml files", e);
        }
    }

    private boolean isMigrationActivated() {
        String activated = System.getProperty(HELMFILE_MIGRATION_ENABLE_KEY);
        return "true".equalsIgnoreCase(activated);
    }

    private boolean isArgoCDApplicationTemplate(File file) throws IOException {
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
