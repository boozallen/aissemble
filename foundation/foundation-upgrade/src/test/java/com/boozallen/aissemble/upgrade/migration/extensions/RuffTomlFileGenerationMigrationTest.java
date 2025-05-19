package com.boozallen.aissemble.upgrade.migration.extensions;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Upgrade
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
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
