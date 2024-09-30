package com.boozallen.aissemble.upgrade.migration.v1_10_0;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Upgrade
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import com.boozallen.aissemble.upgrade.migration.AbstractAissembleMigration;

public class SparkVersionUpgradeMigration extends AbstractAissembleMigration {
    private static final String OLD_FAILURE_VALIDITY_INTERVAL = "spark.yarn.executor.failuresValidityInterval";
    private static final String NEW_FAILURE_VALIDITY_INTERVAL = "spark.executor.failuresValidityInterval";
    private static final String OLD_MAX_FAILURES = "spark.yarn.max.executor.failures";
    private static final String NEW_MAX_FAILURES = "spark.executor.maxNumFailures";

    @Override
    protected boolean shouldExecuteOnFile(File file) {
        try {
            return (Files.readString(file.toPath()).contains(OLD_FAILURE_VALIDITY_INTERVAL) ||
                    Files.readString(file.toPath()).contains(OLD_MAX_FAILURES));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected boolean performMigration(File file) {
        try {
            Files.writeString(file.toPath(), Files.readString(file.toPath())
                    .replace(OLD_FAILURE_VALIDITY_INTERVAL, NEW_FAILURE_VALIDITY_INTERVAL));
            Files.writeString(file.toPath(), Files.readString(file.toPath())
                    .replace(OLD_MAX_FAILURES, NEW_MAX_FAILURES));
            return true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
