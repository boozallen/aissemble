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

import com.boozallen.aissemble.upgrade.migration.version_specific.SparkBomDependencyMigration;

import org.apache.maven.project.MavenProject;

import java.io.File;

public class SparkBomDependencyMigrationTest extends SparkBomDependencyMigration {

    public SparkBomDependencyMigrationTest(File testPom) {
        MavenProject parentAissembleProject = new MavenProject();
        parentAissembleProject.setArtifactId(AISSEMBLE_PARENT);
        parentAissembleProject.setGroupId("com.boozallen.aissemble");

        MavenProject project = new MavenProject();
        project.setArtifactId("simple-project");
        project.setParent(parentAissembleProject);
        project.setFile(testPom);
        setMavenProject(project);
    }
}
