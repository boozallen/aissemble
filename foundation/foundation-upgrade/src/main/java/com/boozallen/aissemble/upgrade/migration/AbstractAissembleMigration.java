package com.boozallen.aissemble.upgrade.migration;

/*-
 * #%L
 * foundation-upgrade
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import org.apache.maven.project.MavenProject;
import org.technologybrewery.baton.AbstractMigration;
import org.technologybrewery.baton.BatonException;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public abstract class AbstractAissembleMigration extends AbstractMigration {
    protected static final String FIRST_REGEX_GROUPING = "$1";
    protected static final String QUOTE = "\"";
    protected static final String AISSEMBLE_PARENT = "build-parent";

    protected String getAissembleVersion() {
        return getMavenProject().getProperties().getProperty("version.aissemble");
    }

    @Override
    protected abstract boolean shouldExecuteOnFile(File file);

    @Override
    protected abstract boolean performMigration(File file);

    protected String getRootArtifactId() {
        MavenProject project = getRootProject();
        if(!project.hasParent()) {
            throw new RuntimeException("The migration is not being run on an aissemble-based project");
        }
        return project.getArtifactId();
    }

    /**
     * Gets the root Maven project, where the root is considered either the project which has no parent, or the project
     * with the aiSSEMBLE build-parent artifact as its parent, whichever comes first.
     *
     * @return the root project
     */
    protected MavenProject getRootProject() {
        MavenProject project = getMavenProject();
        while(project != null && project.getParent() != null && !AISSEMBLE_PARENT.equals(project.getParent().getArtifactId())) {
            project = project.getParent();
        }
        return project;
    }

    /**
     * Deletes a directory if it's empty
     * @param folderPath The directory to be deleted
     * @throws IOException
     */
    protected static void deleteIfEmpty(Path folderPath) throws IOException {
        try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(folderPath)) {
            if (!dirStream.iterator().hasNext()) {
                Files.delete(folderPath);
            }
        }
    }
}
