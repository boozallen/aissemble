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
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.technologybrewery.baton.util.pom.PomHelper;
import org.technologybrewery.baton.util.pom.PomModifications;
import org.technologybrewery.baton.util.pom.PomModifications.Insertion;

import com.boozallen.aissemble.upgrade.migration.AbstractPomMigration;

import static org.technologybrewery.baton.util.pom.LocationAwareMavenReader.END;
import static org.apache.commons.lang3.StringUtils.repeat;

/**
 * Updates a Spark pipeline module pom.xml with the new CDI classes dependency to ensure messaging compatibility with Java 17 
 */
public class SparkPipelineMessagingPomMigration extends AbstractPomMigration {
    private static final Logger logger = LoggerFactory.getLogger(SparkPipelineMessagingPomMigration.class);

    private static final String SMALLRYE_REACTIVE_MESSAGING_GROUP_ID = "io.smallrye.reactive";
    private static final String SMALLRYE_REACTIVE_MESSAGING_KAFKA_ARTIFACT_ID = "smallrye-reactive-messaging-kafka";

    private static final String AISSEMBLE_GROUP_ID = "com.boozallen.aissemble";
    private static final String AISSEMBLE_MESSAGING_KAFKA_ARTIFACT_ID = "extensions-messaging-kafka";
 
    @Override
    protected boolean shouldExecuteOnFile(File pomFile) {
        Model model = PomHelper.getLocationAnnotatedModel(pomFile);

        // Check if the pipeline pom is type jar
        if (model.getPackaging().equals("jar")) {

            // Check if the pipeline pom contains the smallrye-reactive-messaging-kafka dependency and
            // doesn't have the extensions-messaging-kafka dependency
            return this.hasDependency(model, SMALLRYE_REACTIVE_MESSAGING_GROUP_ID, SMALLRYE_REACTIVE_MESSAGING_KAFKA_ARTIFACT_ID)
                   && !this.hasDependency(model, AISSEMBLE_GROUP_ID, AISSEMBLE_MESSAGING_KAFKA_ARTIFACT_ID);

        } else {
            return false;
        }
    }

    @Override
    protected boolean performMigration(File pomFile) {
        logger.info("Migrating file with new CDI messaging dependency: {}", pomFile.getAbsolutePath());
        Model model = PomHelper.getLocationAnnotatedModel(pomFile);

        detectAndSetIndent(pomFile);
        PomModifications modifications = new PomModifications();
        modifications.add(new Insertion(model.getLocation("dependencies" + END), 1, this::getDependencyContent));

        return PomHelper.writeModifications(pomFile, modifications.finalizeMods());
    }

    private String getDependencyContent(String ignore) {
        return repeat(indent, 2) + "<dependency>\n" +
               repeat(indent, 3) + "<groupId>" + AISSEMBLE_GROUP_ID + "</groupId>\n" +
               repeat(indent, 3) + "<artifactId>" + AISSEMBLE_MESSAGING_KAFKA_ARTIFACT_ID + "</artifactId>\n" +
               repeat(indent, 2) + "</dependency>\n";
    }
}
