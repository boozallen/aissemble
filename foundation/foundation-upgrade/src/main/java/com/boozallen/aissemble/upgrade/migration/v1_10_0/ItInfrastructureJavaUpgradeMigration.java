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

import org.technologybrewery.baton.BatonException;

import com.boozallen.aissemble.upgrade.migration.AbstractAissembleMigration;

public class ItInfrastructureJavaUpgradeMigration extends AbstractAissembleMigration {
    private static final String OLD_JDK_VERSION = "FROM openjdk:11-slim";
    private static final String NEW_JDK_VERSION = "FROM openjdk:17-jdk-slim";

    @Override
    protected boolean shouldExecuteOnFile(File file) {
        try {
            return (Files.readString(file.toPath()).contains(OLD_JDK_VERSION));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected boolean performMigration(File file) {
        try {
            Files.writeString(file.toPath(), Files.readString(file.toPath())
                    .replace(OLD_JDK_VERSION, NEW_JDK_VERSION));
            return true;
        } catch (IOException e) {
            throw new BatonException("Failed to update Dockerfile parent image", e);
        }
    }
}
