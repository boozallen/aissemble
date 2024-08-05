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
 Baton migration class to verify whether a migrations are required for any outdated PDP and/or authzforce modules which
 are referencing AIOPS instead of aiSSEMBLE.
 **/

public class ExtensionsSecurityProjectsMigration extends AbstractAissembleMigration {

    public static final Logger logger = LoggerFactory.getLogger(ExtensionsSecurityProjectsMigration.class);
    public static final Map<String, String> EXTENSIONS_SECURITY_REFERENCE_PACKAGE_MAP = Map.of(
            // aissemble/security/authorization/
            "AiopsKeycloakSecureTokenServiceClient","AissembleKeycloakSecureTokenServiceClient",
            "AiopsKeyStore","AissembleKeyStore",
            "AiopsSecureTokenServiceClient","AissembleSecureTokenServiceClient",
            "AiopsSimpleSecureTokenServiceClient","AissembleSimpleSecureTokenServiceClient",

            // aissemble/security/authorization/policy/
            "AiopsAttribute","AissembleAttribute",
            "AiopsAttributePoint","AissembleAttributePoint",
            "AiopsAttributeProvider","AissembleAttributeProvider",
            "AiopsAttributeUtils","AissembleAttributeUtils",

            // aissemble/security/authorization/exception
            "AiopsSecurityException", "AissembleSecurityException"

    );

    /**
     * Function to check whether the migration is necessary.
     * @param file file to check
     * @return shouldExecute - whether the migration is necessary.
     */
    @Override
    protected boolean shouldExecuteOnFile(File file) {
        boolean shouldExecute = false;
        try (BufferedReader aiopsReferenceJavaConfig = new BufferedReader((new FileReader(file)))) {
            String line;
            while((line = aiopsReferenceJavaConfig.readLine()) !=null && !shouldExecute) {
                for (String key : EXTENSIONS_SECURITY_REFERENCE_PACKAGE_MAP.keySet()) {
                    if (line.contains(key)) {
                        shouldExecute = true;
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error in determining whether an 'AIOPS' renamed java package requires a migration.");
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
            for (Map.Entry<String, String> entry : EXTENSIONS_SECURITY_REFERENCE_PACKAGE_MAP.entrySet()) {
                replaceLiteralInFile(file, entry.getKey(), entry.getValue());
                isMigrated = true;
            }
        } catch (Exception e) {
            logger.error("Error in performing the migration for a refactored java package.");
        }
        return isMigrated;
    }
}