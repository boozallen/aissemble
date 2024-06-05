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

import com.boozallen.aissemble.upgrade.migration.AbstractAissembleMigration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.technologybrewery.baton.BatonException;

import java.io.File;
import java.io.IOException;

import static com.boozallen.aissemble.upgrade.util.FileUtils.*;

/**
 * Baton migration used to update the naming convention of project specific aiSSEMBLE generated images.
 */
public class ProjectSpecificImageNameMigration extends AbstractAissembleMigration {

    public static final Logger logger = LoggerFactory.getLogger(ProjectSpecificImageNameMigration.class);

    public static final String ORGANIZATION_PRE_1_7_0 = "boozallen/";
    private String projectName;

    /**
     * Function to check whether the migration is necessary.
     * @param file file to check
     * @return shouldExecute - whether the migration is necessary.
     */
    @Override
    protected boolean shouldExecuteOnFile(File file) {
        boolean shouldExecute;
        try {
            projectName = getProjectPrefix();
            shouldExecute = hasRegExMatch(ORGANIZATION_PRE_1_7_0 + projectName, file);
        } catch (IOException e){
            throw new BatonException("Unable to determine if project specific image name migration must be executed", e);
        }
        return shouldExecute;
    }

    /**
     * Performs the migration if the shouldExecuteOnFile() returns true.
     * @param file file to migrate
     * @return isMigrated - Whether the file was migrated successfully.
     */
    @Override
    protected boolean performMigration(File file) {
        boolean isMigrated;

        try {
            isMigrated = replaceLiteralInFile(file, ORGANIZATION_PRE_1_7_0 + projectName, projectName);
        } catch (Exception e) {
            logger.error("Error in migrating project specific image names.");
            throw new BatonException(e);
        }
        return isMigrated;
    }
}