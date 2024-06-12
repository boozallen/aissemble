package com.boozallen.aissemble.upgrade.migration.utils;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Upgrade
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import org.apache.maven.project.MavenProject;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

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
}
