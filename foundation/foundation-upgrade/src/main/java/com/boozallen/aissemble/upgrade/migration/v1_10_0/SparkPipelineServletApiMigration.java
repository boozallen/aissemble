package com.boozallen.aissemble.upgrade.migration.v1_10_0;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Upgrade
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import java.io.File;

import org.apache.maven.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.technologybrewery.baton.util.pom.PomHelper;
import org.technologybrewery.baton.util.pom.PomModifications;
import org.technologybrewery.baton.util.pom.PomModifications.Insertion;

import com.boozallen.aissemble.upgrade.migration.AbstractPomMigration;

import static org.technologybrewery.baton.util.pom.LocationAwareMavenReader.END;
import static org.apache.commons.lang3.StringUtils.repeat;

/**
 * Updates a Spark pipeline module pom.xml with the javax.servlet-api dependency to ensure compatibility
 * with Apache Spark 3.5 which has not migrated to Jakarta packages yet
 */
public class SparkPipelineServletApiMigration extends AbstractPomMigration {
    private static final Logger logger = LoggerFactory.getLogger(SparkPipelineServletApiMigration.class);
    private static final String JAVAX_SERVLET_GROUP_ID = "javax.servlet";
    private static final String JAVAX_SERVLET_ARTIFACT_ID = "javax.servlet-api";

    @Override
    protected boolean shouldExecuteOnFile(File pomFile) {
        Model model = PomHelper.getLocationAnnotatedModel(pomFile);

        // Check if the pipeline pom is type jar and doesn't have the javax servlet dependency
        return model.getPackaging().equals("jar") && !this.hasDependency(model, JAVAX_SERVLET_GROUP_ID, JAVAX_SERVLET_ARTIFACT_ID);
    }

    @Override
    protected boolean performMigration(File pomFile) {
        logger.info("Migrating file to include the Javax Servlet API dependency: {}", pomFile.getAbsolutePath());
        Model model = PomHelper.getLocationAnnotatedModel(pomFile);

        this.detectAndSetIndent(pomFile);
        PomModifications modifications = new PomModifications();
        modifications.add(new Insertion(model.getLocation("dependencies" + END), 1, this::getDependencyContent));

        return PomHelper.writeModifications(pomFile, modifications.finalizeMods());
    }
    
    private String getDependencyContent(String ignore) {
        return repeat(indent, 2) + "<dependency>\n" +
               repeat(indent, 3) + "<!-- Spark hasn't migrated to Jakarta packages yet. Will need to shade it if we want to fully migrate. -->\n" +                                      "" + 
               repeat(indent, 3) + "<!-- See https://github.com/apache/incubator-hugegraph-toolchain/issues/464 -->\n" + 
               repeat(indent, 3) + "<groupId>" + JAVAX_SERVLET_GROUP_ID + "</groupId>\n" +
               repeat(indent, 3) + "<artifactId>" + JAVAX_SERVLET_ARTIFACT_ID + "</artifactId>\n" +
               repeat(indent, 3) + "<version>${version.javax.servlet}</version>\n" +
               repeat(indent, 2) + "</dependency>\n";
    }
}
