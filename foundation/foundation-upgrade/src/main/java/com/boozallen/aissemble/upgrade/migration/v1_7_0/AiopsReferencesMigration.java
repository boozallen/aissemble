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
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.boozallen.aissemble.upgrade.util.FileUtils.replaceLiteralInFile;

public class AiopsReferencesMigration extends AbstractAissembleMigration {

    public static final Logger logger = LoggerFactory.getLogger(AiopsReferencesMigration.class);
    public static final Map<String, String> AIOPS_REFERENCE_PACKAGE_MAP = Map.of(
        "com.boozallen.aiops.data.lineage", "com.boozallen.aissemble.data.lineage",
        "com.boozallen.aiops.core.cdi", "com.boozallen.aissemble.core.cdi",
        "com.boozallen.aiops.core.metadata", "com.boozallen.aissemble.core.metadata",
        "com.boozallen.aiops.core.filestore", "com.boozallen.aissemble.core.filestore"
    );

    /**
     * Function to check whether the migration is necessary.
     * @param file file to check
     * @return shouldExecute - whether the migration is necessary.
     */
    @Override
    protected boolean shouldExecuteOnFile(File file) {
        boolean shouldExecute = false;
        try (BufferedReader aiopsReferenceConfig = new BufferedReader((new FileReader(file)))) {

            String line;
            while((line = aiopsReferenceConfig.readLine()) !=null && !shouldExecute) {
                for (String key : AIOPS_REFERENCE_PACKAGE_MAP.keySet()) {
                    if (line.contains(key)) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error in determining whether a 'AIOPS' renamed package requires a migration.");
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
            for (Map.Entry<String, String> entry : AIOPS_REFERENCE_PACKAGE_MAP.entrySet()) {
                    replaceLiteralInFile(file, entry.getKey(), entry.getValue());
            }
        } catch (Exception e) {
            throw new BatonException("Error in migrating renamed 'AIOPS' package.", e);
        }
        return isMigrated;
    }
}