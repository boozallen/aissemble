package com.boozallen.aissemble.upgrade.migration;

/*-
 * #%L
 * foundation-upgrade
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
