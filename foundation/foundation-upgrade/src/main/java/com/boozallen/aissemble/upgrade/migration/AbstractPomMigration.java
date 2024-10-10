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
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.technologybrewery.baton.BatonException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.stream.Stream;

public abstract class AbstractPomMigration extends AbstractAissembleMigration {
    protected static final Logger logger = LoggerFactory.getLogger(AbstractPomMigration.class);

    public static final String POM = "pom";
    public static final String GUARANTEED_TAG = "<modelVersion>";

    protected String indent;
    
    protected void detectAndSetIndent(File file) {
        try (Stream<String> lines = Files.lines(file.toPath())) {
            indent = lines.filter(line -> line.contains(GUARANTEED_TAG))
                    .findFirst()
                    .map(artifact -> artifact.substring(0, artifact.indexOf(GUARANTEED_TAG)))
                    .orElse(null);
            if (StringUtils.isEmpty(indent)) {
                logger.info("Failed to detect indent for POM. Using default. {}", file);
                indent = "    ";
            }
        } catch (IOException e) {
            throw new BatonException("Failed to get indent from POM:" + file, e);
        }
    }
}