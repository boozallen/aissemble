package com.boozallen.aissemble.upgrade.migration.v1_8_0;

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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import java.util.Map;

import static org.technologybrewery.baton.util.FileUtils.replaceLiteralInFile;

/**
 Baton migration class to verify whether a migration to the python file packages are required from the aiops reference removal from the
 foundation-core-python module.
**/

public class AiopsReferencePythonMigration extends AbstractAissembleMigration {

    public static final Logger logger = LoggerFactory.getLogger(AiopsReferencePythonMigration.class);
    public static final Map<String, String> AIOPS_REFERENCE_PYTHON_PACKAGE_MAP = Map.of(
            "aiops.authority", "aissemble.authority",
            "aiopskey" , "aissemblekey",
            "/deployments/aissemble-secure.jks" , "/deployments/aissemble-secure.jks",
            "policy-decision-point.aiops-security.properties.vm", "policy-decision-point.aissemble-security.properties.vm",
            "aiops_core_filestore.file_store_factory","aissemble_core_filestore.file_store_factory",
            "aiops_core_metadata.metadata_model", "aissemble_core_metadata.metadata_model",
            "aiops_core_metadata.metadata_api", "aissemble_core_metadata.metadata_api",
            "aiops_security.pdp_client", "aissemble_security.pdp_client",
            "AiopsSecurityException", "AissembleSecurityException",
            "aiopsauth.auth_config", "aissembleauth.auth_config"
    );

    /**
     * Function to check whether the migration is necessary.
     * @param file file to check
     * @return shouldExecute - whether the migration is necessary.
     */
    @Override
    protected boolean shouldExecuteOnFile(File file) {
        boolean shouldExecute = false;
        try (BufferedReader aiopsReferencePythonConfig = new BufferedReader((new FileReader(file)))) {
            String line;
            while((line = aiopsReferencePythonConfig.readLine()) !=null && !shouldExecute) {
                for (String key : AIOPS_REFERENCE_PYTHON_PACKAGE_MAP.keySet()) {
                    if (line.contains(key)) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error in determining whether a 'AIOPS' renamed python package requires a migration.");
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
            for (Map.Entry<String, String> entry : AIOPS_REFERENCE_PYTHON_PACKAGE_MAP.entrySet()) {
                replaceLiteralInFile(file, entry.getKey(), entry.getValue());
            }
        } catch (Exception e) {
            logger.error("Error in performing the migration for a refactored python package.");
        }
        return isMigrated;
    }
}