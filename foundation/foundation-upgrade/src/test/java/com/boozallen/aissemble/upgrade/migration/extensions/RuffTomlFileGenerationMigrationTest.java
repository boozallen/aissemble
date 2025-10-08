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

import com.boozallen.aissemble.upgrade.migration.version_specific.RuffTomlFileGenerationMigration;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class RuffTomlFileGenerationMigrationTest extends RuffTomlFileGenerationMigration {

    public RuffTomlFileGenerationMigrationTest(File testPom) throws IOException, XmlPullParserException {
        Model model = new MavenXpp3Reader().read(new FileReader(testPom));
        MavenProject project = new MavenProject(model);
        project.setFile(testPom);
        MavenProject habushuMavenProject = new MavenProject();
        habushuMavenProject.setPackaging("habushu");
        project.setCollectedProjects(List.of(habushuMavenProject));
        setMavenProject(project);
    }
}
