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

import org.apache.commons.io.FileUtils;
import org.technologybrewery.baton.BatonException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;


/**
 * Migration to update the aissemble version used in the helmfile
 */
public class HelmTemplatesMigration extends AbstractHelmfileMigration {
    /**
     * Migration will move the file to the apps/common-infrastructure folder
     * @param file helmfile values file to migrate
     * @return true if migration was successfully performed
     */
    @Override
    protected boolean performMigration(File file) {
        boolean isMigrated = false;

        try {
            Path targetPath = Paths.get(file.getParentFile().getParentFile().getCanonicalPath(),
                    "apps", "common-infrastructure", "templates");

            FileUtils.moveFileToDirectory(file, targetPath.toFile(), true);

            // After moving the current yaml file we want to check
            // if the templates folder is now empty and delete
            deleteIfEmpty(Paths.get(file.getParentFile().getAbsolutePath()));

            isMigrated = true;
        } catch (IOException e) {
            throw new BatonException("Could not migrate file: " + file.getPath(), e);
        }
        return isMigrated;
    }

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
            // In our case we want to migrate files that are not argocd
            return !isArgoCDApplicationTemplate(file);
        } catch (Exception e) {
            throw new BatonException("Could not check yaml file for ArgoCD removal: " + file.getPath(), e);
        }
    }



}
