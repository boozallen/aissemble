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

import com.boozallen.aissemble.upgrade.migration.AbstractAissembleMigration;
import org.apache.maven.model.Dependency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.regex.Pattern;

public class AlertingCdiMigration extends AbstractAissembleMigration {
    public static final Logger logger = LoggerFactory.getLogger(AlertingCdiMigration.class);
    public static final String GROUP = "com.boozallen.aissemble" ;
    public static final String CDI_CLASS = "AlertingCdiContext" ;
    public static final String FQCN = GROUP + ".alerting.core.cdi." + CDI_CLASS;

    @Override
    protected boolean shouldExecuteOnFile(File file) {
        return "CdiContainerFactory.java".equals(file.getName())
                && dependsOnAlerting()
                && !addsAlerting(file);
    }

    @Override
    protected boolean performMigration(File file) {
        String knownContext = "PipelinesCdiContext"; // a context we know exists and is being added
        // " *[something].add( *new PipelinesCdiContext() *);" capturing up to/excluding "new" and capturing after the constructor
        // e.g. [    mylist.add(  ]new PipelinesCdiContext()[  );] -- with captured text between `[]`
        Pattern contextAddPattern = Pattern.compile(".*?\\.add\\(\\s*new " + knownContext + "\\(\\)\\s*\\);");
        File tempFile = new File(file.getParent(), file.getName() + ".tmp");
        boolean addedImport = false;
        boolean addedContext = false;
        try(BufferedReader reader = Files.newBufferedReader(file.toPath());
            BufferedWriter writer = Files.newBufferedWriter(tempFile.toPath())) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!addedImport && line.startsWith("import " + GROUP)) {
                    writer.write("import " + FQCN + ";");
                    writer.newLine();
                    addedImport = true;
                } else if (addedImport && !addedContext && contextAddPattern.matcher(line).matches()) {
                    writer.write(line.replace(knownContext, CDI_CLASS));
                    writer.newLine();
                    addedContext = true;
                }
                writer.write(line);
                writer.newLine();
            }
            //We might add the import but not the context list update. That's OK and helps guide the user to fixing manually
            Files.move(tempFile.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update CdiContainerFactory with " + CDI_CLASS, e);
        }
        if (!addedImport) {
            logger.warn("Could not detect import injection point for " + FQCN);
            logger.warn("Migration failed for " + file.getPath());
        } else if(!addedContext) {
            logger.warn("Could not detect context list injection point for " + CDI_CLASS);
            logger.warn("Migration partially failed for " + file.getPath());
        }
        return addedContext;
    }

    private boolean addsAlerting(File file) {
        boolean imported = false;
        boolean constructed = false;
        try (BufferedReader reader = Files.newBufferedReader(file.toPath())) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("import " + FQCN)) {
                    imported = true;
                } else if (line.contains("new " + FQCN)) {
                    constructed = true;
                } else if (imported && line.contains("new " + CDI_CLASS)) {
                    constructed = true;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to inspect CdiContainerFactory class contents to determine if migration is needed", e);
        }
        return constructed;
    }

    private boolean dependsOnAlerting() {
        return getMavenProject()
                .getDependencies().stream()
                .anyMatch(this::isAlerting);
    }

    private boolean isAlerting(Dependency dependency) {
    return GROUP.equals(dependency.getGroupId())
            && "foundation-alerting".equals(dependency.getArtifactId());
    }
}
