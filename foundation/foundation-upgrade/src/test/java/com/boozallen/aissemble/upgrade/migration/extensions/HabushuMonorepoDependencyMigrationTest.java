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

import org.apache.maven.project.MavenProject;

import java.util.ArrayList;
import java.util.List;

import com.boozallen.aissemble.upgrade.migration.version_specific.HabushuMonorepoDependencyMigration;

/**
 * HabushuMonorepoDependencyMigrationTest
 */
public class HabushuMonorepoDependencyMigrationTest extends HabushuMonorepoDependencyMigration {
    private static final String TEST_GROUPID = "com.boozallen.test";
    public static final String TEST_VERSION = "1.0.0-SNAPSHOT";

    private List<MavenProject> projects = new ArrayList<>();

    public void addProject(String artifactId, String packaging) {
        MavenProject project = new MavenProject();
        project.setGroupId(TEST_GROUPID);
        project.setArtifactId(artifactId);
        project.setVersion(TEST_VERSION);
        project.setPackaging(packaging);
        projects.add(project);
    }

    @Override
    public void setMavenProject(MavenProject project) {
        super.setMavenProject(project);
        projects.add(project);
    }

    @Override
    protected MavenProject getRootProject() {
        return new MavenProject() {
            @Override
            public List<MavenProject> getCollectedProjects() {
                return projects;
            }
        };
    }
}

