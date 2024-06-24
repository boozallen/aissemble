package com.boozallen.aissemble.upgrade.migration.v1_9_0;

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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Migration updating the default thrift endpoint for the Data Access service in accordance with the 1.9.0 spark infrastructure
 * helm chart changes.  Functionally just changes the host from `spark-infrastructure:10001` to `spark-infrastructure-sts-service:10001`.
 * Will not execute if the project has previously changed from the default endpoint host.
 */
public class UpdateDataAccessThriftServerEndpointMigration extends AbstractAissembleMigration {
    public static final String OLD_THRIFT_ENDPOINT = "jdbc:hive2://spark-infrastructure:10001/default;transportMode=http;httpPath=cliservice";
    public static final String NEW_THRIFT_ENDPOINT = "jdbc:hive2://spark-infrastructure-sts-service:10001/default;transportMode=http;httpPath=cliservice";

    @Override
    protected boolean shouldExecuteOnFile(File file) {
        try {
            return Files.readString(file.toPath()).contains(OLD_THRIFT_ENDPOINT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected boolean performMigration(File file) {
        try {
            Files.writeString(file.toPath(), Files.readString(file.toPath()).replace(OLD_THRIFT_ENDPOINT, NEW_THRIFT_ENDPOINT));
            return true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
