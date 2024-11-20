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
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.model.Model;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.technologybrewery.baton.BatonException;
import org.technologybrewery.baton.util.FileUtils;

import com.boozallen.aissemble.upgrade.migration.AbstractPomMigration;


/**
 * Updates a Spark pipeline module CdiContainerFactory.java with the new CDI classes to ensure messaging compatibility with Java 17
 */
public class SparkPipelineMessagingCdiFactoryMigration extends AbstractPomMigration {
    private static final Logger logger = LoggerFactory.getLogger(SparkPipelineMessagingCdiFactoryMigration.class);

    private static final String SMALLRYE_REACTIVE_MESSAGING_GROUP_ID = "io.smallrye.reactive";
    private static final String SMALLRYE_REACTIVE_MESSAGING_ARTIFACT_ID = "smallrye-reactive-messaging";
    private static final String SMALLRYE_REACTIVE_MESSAGING_KAFKA_ARTIFACT_ID = "smallrye-reactive-messaging-kafka";

    private static final String AISSEMBLE_CDI_CONTAINER_IMPORT = "import com.boozallen.aissemble.core.cdi.CdiContainer;";
    private static final String AISSEMBLE_MESSAGING_CDI_IMPORT = "import com.boozallen.aissemble.messaging.core.cdi.MessagingCdiContext;";
    private static final String AISSEMBLE_MESSAGING_CDI_OBJECT = "new MessagingCdiContext()";
    private static final String AISSEMBLE_KAFKA_MESSAGING_CDI_IMPORT = "import com.boozallen.aissemble.kafka.context.KafkaConnectorCdiContext;";
    private static final String AISSEMBLE_KAFKA_MESSAGING_CDI_OBJECT = "new KafkaConnectorCdiContext()";
    private static final String AISSEMBLE_PIPELINE_CDI_OBJECT = "new PipelinesCdiContext()";
 
    @Override
    protected boolean shouldExecuteOnFile(File cdiContainerFactoryFile) {
        // Check if the pipeline pom contains any smallrye-reactive-messaging dependencies
        Model model = this.getMavenProject().getModel();

        if (this.hasDependency(model, SMALLRYE_REACTIVE_MESSAGING_GROUP_ID, SMALLRYE_REACTIVE_MESSAGING_ARTIFACT_ID) 
            && model.getPackaging().equals("jar")) {

            // Check if the pipeline pom contains the smallrye-reactive-messaging-kafka dependency
            boolean addKafkaCdiContext = this.hasDependency(model, SMALLRYE_REACTIVE_MESSAGING_GROUP_ID, SMALLRYE_REACTIVE_MESSAGING_KAFKA_ARTIFACT_ID);

            // Check if only the CdiContainerFactory.java needs to be migrated
            return shouldMigrateCdiContainerFactoryFile(cdiContainerFactoryFile, addKafkaCdiContext);
        } else {
            return false;
        }
    }

    @Override
    protected boolean performMigration(File cdiContainerFactoryFile) {
        Model model = this.getMavenProject().getModel();

        // Check if the pipeline pom contains the smallrye-reactive-messaging-kafka dependency
        boolean addKafkaCdiContext = this.hasDependency(model, SMALLRYE_REACTIVE_MESSAGING_GROUP_ID, SMALLRYE_REACTIVE_MESSAGING_KAFKA_ARTIFACT_ID);
        
        // Update the file with the new import(s) and CDI context object(s)
        return this.migrateCdiContainerFactoryFile(cdiContainerFactoryFile, addKafkaCdiContext);
    }

    /* 
     * Determine if the CdiContainerFactory.java needs to be migrated
     */
    private boolean shouldMigrateCdiContainerFactoryFile(File file, boolean addKafkaCdiContext) {
        try {
            // Check if the new imports already exist
            boolean hasMessagingCdiImport = Files.readString(file.toPath()).contains(AISSEMBLE_MESSAGING_CDI_IMPORT);
            boolean hasKafkaCdiImport = Files.readString(file.toPath()).contains(AISSEMBLE_KAFKA_MESSAGING_CDI_IMPORT);

            return !hasMessagingCdiImport || (!hasKafkaCdiImport && addKafkaCdiContext);
        } catch (IOException e) {
            throw new BatonException("Failed to read CDI container file: " + file.getAbsolutePath(), e);
        }
    }

    /*
     * Migrate the CdiContainerFactory.java to add the new context object(s) and respective import(s)
     */
    private boolean migrateCdiContainerFactoryFile(File file, boolean addKafkaCdiContext) {
        logger.info("Migrating file with new CDI context object(s): {}", file.getAbsolutePath());
        try {
            List<String> newFileContents = new ArrayList<>();
            List<String> originalFile = FileUtils.readAllFileLines(file);

            boolean cdiContextAdded = false;
            boolean cdiImportAdded = false;
            boolean hasMessagingCdiImport = Files.readString(file.toPath()).contains(AISSEMBLE_MESSAGING_CDI_IMPORT);
            boolean hasKafkaCdiImport = Files.readString(file.toPath()).contains(AISSEMBLE_KAFKA_MESSAGING_CDI_IMPORT);

            // Iterate through the file
            for (String line : originalFile) {

                // Prepend the necessary imports to the CdiContainer import - should always be present
                if (line.equals(AISSEMBLE_CDI_CONTAINER_IMPORT)) {
                    if (!hasMessagingCdiImport) {
                        newFileContents.add(AISSEMBLE_MESSAGING_CDI_IMPORT);
                    }
                    
                    if (!hasKafkaCdiImport && addKafkaCdiContext) {
                        newFileContents.add(AISSEMBLE_KAFKA_MESSAGING_CDI_IMPORT);
                    }
                    cdiImportAdded = true;
                }
                // Append the necessary CDI Java objects to the getContexts() method
                else if (line.contains(".add(" + AISSEMBLE_PIPELINE_CDI_OBJECT + ");")) {
                    if (!hasMessagingCdiImport) {
                        newFileContents.add(line.replace(AISSEMBLE_PIPELINE_CDI_OBJECT, AISSEMBLE_MESSAGING_CDI_OBJECT));
                    }

                    if (!hasKafkaCdiImport && addKafkaCdiContext) {
                        newFileContents.add(line.replace(AISSEMBLE_PIPELINE_CDI_OBJECT, AISSEMBLE_KAFKA_MESSAGING_CDI_OBJECT));
                    }
                    cdiContextAdded = true;
                }

                newFileContents.add(line);
            }
            
            FileUtils.writeFile(file, newFileContents);
            return cdiImportAdded && cdiContextAdded;
        } catch (IOException e) {
            throw new BatonException("Failed to update the file with the new CDI context object(s): " + file.getAbsolutePath(), e);
        }
    }
}
