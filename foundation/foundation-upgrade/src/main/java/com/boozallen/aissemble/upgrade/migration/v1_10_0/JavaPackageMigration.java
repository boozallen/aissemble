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
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.technologybrewery.baton.BatonException;

import com.boozallen.aissemble.upgrade.migration.AbstractAissembleMigration;

import static java.util.Map.entry; 

/**
 * Updates the affected java classes from their old package name to their new package name 
 * to ensure compatibility with the updated Java 17 dependencies 
 */
public class JavaPackageMigration extends AbstractAissembleMigration {
    private static final Logger logger = LoggerFactory.getLogger(JavaPackageMigration.class);
    private static final Map<String, String> JAVA_PACKAGES = Map.ofEntries(
        entry("javax.activation", "jakarta.activation"),
        entry("javax.annotation", "jakarta.annotation"),
        entry("javax.batch", "jakarta.batch"),
        entry("javax.data", "jakarta.data"),
        entry("javax.decorator", "jakarta.decorator"),
        entry("javax.ejb", "jakarta.ejb"),
        entry("javax.el", "jakarta.el"),
        entry("javax.enterprise", "jakarta.enterprise"),
        entry("javax.faces", "jakarta.faces"),
        entry("javax.inject", "jakarta.inject"),
        entry("javax.interceptor", "jakarta.interceptor"),
        entry("javax.jms", "jakarta.jms"),
        entry("javax.json", "jakarta.json"),
        entry("javax.mail", "jakarta.mail"),
        entry("javax.persistence", "jakarta.persistence"),
        entry("javax.resource", "jakarta.resource"),
        entry("javax.security", "jakarta.security"),
        entry("javax.servlet", "jakarta.servlet"),
        entry("javax.transaction", "jakarta.transaction"),
        entry("javax.validation", "jakarta.validation"),
        entry("javax.websocket", "jakarta.websocket"),
        entry("javax.ws", "jakarta.ws"),
        entry("io.smallrye.reactive.messaging.providers.connectors.InMemoryConnector", "io.smallrye.reactive.messaging.memory.InMemoryConnector"),
        entry("io.smallrye.reactive.messaging.providers.connectors.InMemorySink", "io.smallrye.reactive.messaging.memory.InMemorySink")
    ); 

    @Override
    protected boolean shouldExecuteOnFile(File file) {
        try {
            String fileContent = Files.readString(file.toPath());

            return JAVA_PACKAGES.keySet().stream()
                .anyMatch(packageName -> fileContent.contains(packageName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected boolean performMigration(File file) {
        logger.info("Migrating file to update java class packages: {}", file.getAbsolutePath());
        try {
            String fileContent = Files.readString(file.toPath());

            for (Map.Entry<String, String> packageEntry : JAVA_PACKAGES.entrySet()) {
                fileContent = fileContent.replace(packageEntry.getKey(), packageEntry.getValue());
            }

            Files.writeString(file.toPath(), fileContent);
            return true;
        } catch (IOException e) {
            throw new BatonException("Failed to update Java file with new class package", e);
        }
    }
    
}
