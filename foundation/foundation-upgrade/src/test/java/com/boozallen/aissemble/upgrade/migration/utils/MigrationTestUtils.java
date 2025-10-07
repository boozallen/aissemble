package com.boozallen.aissemble.upgrade.migration.utils;

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
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.yaml.snakeyaml.Yaml;

public final class MigrationTestUtils {

    private static final String AISSEMBLE_PARENT = "build-parent";

    /**
     *
     * @param file The yaml file to extract contents from
     * @return a <String, Object> HashMap containing the contents of the yaml file as key-pair values
     */
    public static HashMap<String, Object> extractYamlContents(File file) throws IOException {

        if (file != null && file.exists()) {
            InputStream fileStream;

            fileStream = com.google.common.io.Files.asByteSource(file).openStream();
            Yaml helmChartYaml = new Yaml();
            return helmChartYaml.load(fileStream);
        }

        throw new IOException("File to parse yaml contents for not found");
    }

    /**
     * Creates a test maven project (and parent maven project). Useful if migration logic requires
     * information (name, artifactId, etc.) from the downstream project.
     * @param projectName Name of the test project
     * @return a MavenProject testProject - downstream maven project
     */
    public static MavenProject createTestMavenProject(String projectName) {
        MavenProject testParentProject = new MavenProject();
        testParentProject.setArtifactId(AISSEMBLE_PARENT);
        MavenProject testProject = new MavenProject();
        testProject.setName(projectName);
        testProject.setArtifactId(projectName);
        testProject.setParent(testParentProject);

        return testProject;
    }

    /**
     * Created a MavenProject from a pom file. Can be used to set a migrations MavenProject to be equal to a test pom
     * file, preventing the need to manually instantiate and set the mavenProject.
     * NOTE: the resulting maven project will only have its model populated. It is not trivial to populate a full
     * MavenProject with all the context from a pom file
     *
     * @return MavenProject equivalent of a pom file
     */
    public static MavenProject createMavenProjectFromPom(File pomfile) throws IOException, XmlPullParserException {
        Model model = new MavenXpp3Reader().read(new FileReader(pomfile));
        return new MavenProject(model);
    }
}
