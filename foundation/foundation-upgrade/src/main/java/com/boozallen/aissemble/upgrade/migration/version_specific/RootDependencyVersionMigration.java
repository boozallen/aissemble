package com.boozallen.aissemble.upgrade.migration.version_specific;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Upgrade
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

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.technologybrewery.baton.BatonException;
import org.technologybrewery.baton.util.pom.PomHelper;
import org.technologybrewery.baton.util.pom.PomModifications;

import com.boozallen.aissemble.upgrade.migration.AbstractPomMigration;

public class RootDependencyVersionMigration extends AbstractPomMigration {

    private static final String AISSEMBLE_GROUP_ID = "com.boozallen.aissemble";
    // Dependency has aissemble groupId, and artifactId, and is not using the version.aissemble property
    private static final String DEPENDENCY_REGEX = "<dependency>\\s*<groupId>com.boozallen.aissemble<\\/groupId>\\s*<artifactId>(.*?)<\\/artifactId>\\s*<version>(?!\\$\\{version\\.aissemble\\})(.*?)<\\/version>";

    /**
     * Determines if the migration should execute on the given file.
     * Will execute if the pom is the root pom file
     * and if the aissemble dependency versions have not already been updated
     *
     * @param pomFile file to check
     * @return true if the migration should execute
     */
    @Override
    protected boolean shouldExecuteOnFile(File pomFile) {
        if (getRootProject().equals(getMavenProject())) {
            try {
                return aissembleDependencyVersionIsNotProperty(pomFile);
            } catch (IOException e) {
                throw new BatonException("Failed to traverse POM file", e);
            }
        }
        return false;
    }

    @Override
    protected boolean performMigration(File pomFile) {
        Model model = PomHelper.getLocationAnnotatedModel(pomFile);
        PomModifications pomModifications = new PomModifications();
        for (Dependency dependency : model.getDependencies()) {
            if (AISSEMBLE_GROUP_ID.equals(dependency.getGroupId())) {
                pomModifications.add(replaceInTag(dependency, "version", "${version.aissemble}"));
            }
        }
        return PomHelper.writeModifications(pomFile, pomModifications.finalizeMods());
    }

    private boolean aissembleDependencyVersionIsNotProperty(File pomFile) throws IOException {
        Charset charset = StandardCharsets.UTF_8;
        String fileContent = Files.readString(pomFile.toPath(), charset);
        Pattern pattern = Pattern.compile(DEPENDENCY_REGEX);
        Matcher matcher = pattern.matcher(fileContent);

        return matcher.find();
    }
}
