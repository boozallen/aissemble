package com.boozallen.aissemble.upgrade.migration;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Upgrade
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import org.technologybrewery.baton.util.FileUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.technologybrewery.baton.BatonException;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.technologybrewery.baton.util.FileUtils.getRegExCaptureGroups;

/**
 * Baton migration used to migrate the Helm Chart names and/or Helm module names in the Chart.yaml and values.yaml files within a project's deploy folder
 */
public abstract class AbstractHelmNameMigration extends AbstractAissembleMigration{
    private static final Logger logger = LoggerFactory.getLogger(AbstractHelmNameMigration.class);

    @Override
    protected boolean shouldExecuteOnFile(File file) {
        boolean shouldExecute = false;
        try {
            List<String> namesToUpdate = getRegExCaptureGroups(getReplacementRegex(file), file);
            shouldExecute = CollectionUtils.isNotEmpty(namesToUpdate);
        } catch (IOException e){
            throw new BatonException("Unable to determine if Helm chart migration must be executed", e);
        }
        return shouldExecute;
    }

    @Override
    protected boolean performMigration(File file) {
        boolean performedSuccessfully = false;
        try {
            performedSuccessfully = FileUtils.replaceInFile(
                    file,
                    getReplacementRegex(file),
                    getReplacementText(file)
            );
        } catch (Exception e) {
            logger.error("Unable to perform Helm chart migration due to exception", e);
        }
        return performedSuccessfully;
    }

    public abstract String getReplacementRegex(File file);
    public abstract String getReplacementText(File file);

}