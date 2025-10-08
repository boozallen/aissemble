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

import org.apache.maven.model.Scm;
import org.apache.maven.project.MavenProject;

import com.boozallen.aissemble.upgrade.migration.version_specific.HelmfileDeploymentScriptMigration;

public class HelmfileDeploymentScriptMigrationTest extends HelmfileDeploymentScriptMigration {


    @Override
    protected MavenProject getRootProject() {
        MavenProject parentAissembleProject = new MavenProject();
        parentAissembleProject.setArtifactId("build-parent");

        MavenProject project = new MavenProject();
        project.setArtifactId("test-project");
        project.setParent(parentAissembleProject);
        project.setName("test-project");
        Scm scm = new Scm();
        scm.setUrl("test.com/test-project");
        scm.setTag("HEAD");
        project.setScm(scm);

        return project;
    }
}
