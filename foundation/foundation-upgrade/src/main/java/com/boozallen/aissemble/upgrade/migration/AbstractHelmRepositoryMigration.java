package com.boozallen.aissemble.upgrade.migration;

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

import org.technologybrewery.baton.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;

/**
 * Baton migration used to migrate the Helm Chart repository URL in the Chart.yaml within a project's deploy folder.
 * Migration is triggered by setting the oldHelmRepositoryUrl system property.
 */
public abstract class AbstractHelmRepositoryMigration extends AbstractAissembleMigration{
    public static final Logger logger = LoggerFactory.getLogger(AbstractHelmRepositoryMigration.class);
    private static final String OLD_HELM_REPO_PROPERTY = "oldHelmRepositoryUrl";

    protected String getOldHelmRepositoryUrl(){
        String oldHelmRepositoryUrl = System.getProperty(OLD_HELM_REPO_PROPERTY);
        if (oldHelmRepositoryUrl!= null){
            return oldHelmRepositoryUrl;
        } else {
            logger.info("oldHelmRepositoryUrl system property not set. Skipping helm chart repository URL migration.");
            return null;
        }
    }

    /**
     * Function that checks if user has provided system property containing old Helm repository URL
     * If yes, then Helm Charts repository URL migration will be run.
     * Otherwise, migration is skipped.
     */
    @Override
    protected boolean shouldExecuteOnFile(File file) {
        boolean shouldExecute = false;
        if (getOldHelmRepositoryUrl() != null){
            shouldExecute = true;
        }
        return shouldExecute;
    }

    @Override
    protected boolean performMigration(File file) {
        boolean performedSuccessfully = false;
        try {
            performedSuccessfully = FileUtils.replaceInFile(
                    file,
                    getReplacementRegex(),
                    getReplacementText()
            );
        } catch (Exception e) {
            logger.error("Unable to perform helm chart repository URL migration due to exception", e);
        }
        return performedSuccessfully;
    }

    public abstract String getReplacementRegex();
    public abstract String getReplacementText();
}
