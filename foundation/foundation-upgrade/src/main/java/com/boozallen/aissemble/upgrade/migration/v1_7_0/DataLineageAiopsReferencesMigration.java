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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import static com.boozallen.aissemble.upgrade.util.FileUtils.replaceLiteralInFile;

public class DataLineageAiopsReferencesMigration extends AbstractAissembleMigration {

    public static final Logger logger = LoggerFactory.getLogger(DataLineageAiopsReferencesMigration.class);

    public static final String VERSION_AISSEMBLE_POST_1_7_0 = "com.boozallen.aissemble.data.lineage";

    public static final String VERSION_AISSEMBLE_PRE_1_7_0 = "com.boozallen.aiops.data.lineage";

    /**
     * Function to check whether the migration is necessary.
     * @param file file to check
     * @return shouldExecute - whether the migration is necessary.
     */
    @Override
    protected boolean shouldExecuteOnFile(File file) {
        boolean shouldExecute = false;
        try (BufferedReader dataLineageConfig = new BufferedReader((new FileReader(file)))) {

            String line;
            while((line = dataLineageConfig.readLine()) !=null && !shouldExecute) {
                if (line.contains(VERSION_AISSEMBLE_PRE_1_7_0)) {
                    shouldExecute = true;
                }
            }
        } catch (Exception e) {
            logger.error("Error in determining whether the Data Lineage module requires a migration.");
            throw new BatonException(e);
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
        boolean isMigrated = false;

        try {
            replaceLiteralInFile(file, VERSION_AISSEMBLE_PRE_1_7_0, VERSION_AISSEMBLE_POST_1_7_0);
        } catch (Exception e) {
            logger.error("Error in migrating Data Lineage module.");
            throw new BatonException(e);
        }
        return isMigrated;
    }
}