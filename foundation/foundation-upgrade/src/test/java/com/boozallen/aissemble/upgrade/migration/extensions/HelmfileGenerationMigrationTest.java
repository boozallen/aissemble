package com.boozallen.aissemble.upgrade.migration.extensions;

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
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.maven.project.MavenProject;

import com.boozallen.aissemble.upgrade.migration.version_specific.HelmfileGenerationMigration;

public class HelmfileGenerationMigrationTest extends HelmfileGenerationMigration {

    private static final Path TEST_FILES_FOLDER = Paths.get("target", "test-classes", "test-files");

    @Override
    protected MavenProject getRootProject() {
        MavenProject parentAissembleProject = new MavenProject();
        parentAissembleProject.setArtifactId("build-parent");

        MavenProject project = new MavenProject();
        project.setArtifactId("test-pipelines");
        project.setParent(parentAissembleProject);
        project.setFile(getTestFile(Path.of("version-specific", "HelmfileGenerationMigration", "migration",
                "pom.xml").toString()));

        return project;
    }

    @Override
    public String getAissembleVersion() {
        return "1.12.1";
    }

    protected static File getTestFile(String subPath) {
        if (subPath.startsWith(File.separator)) {
            subPath = subPath.substring(1);
        }
        File testFile = TEST_FILES_FOLDER.resolve(subPath).toFile();
        File dir = testFile.getParentFile();
        if (!dir.mkdirs() && !dir.isDirectory()) {
            throw new RuntimeException("Parent directory of test file is already a regular file: " + dir);
        }
        return testFile;
    }
}
