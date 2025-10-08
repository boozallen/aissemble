package com.boozallen.aissemble.upgrade.migration.version_specific;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Upgrade
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import static org.technologybrewery.baton.util.FileUtils.readAllFileLines;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.technologybrewery.baton.BatonException;

/**
 * This migration removes the ArgoCD application templates and the associated Chart.yaml, Chart.lock, and values*.yaml
 * files if the helmfile migration enable key is set to true.
 */
public class ArgoCDRemovalMigration extends AbstractHelmfileMigration {


    /**
     * Determines whether the migration should execute.
     * if the helmfile migration enable key is set and file is an ArgoCD application template, the migration will run.
     *
     * @param file yaml file to check
     * @return true if the migration should run
     */
    @Override
    protected boolean shouldExecuteOnFileImpl(File file) {
        try {
            return isArgoCDApplicationTemplate(file);
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
}
