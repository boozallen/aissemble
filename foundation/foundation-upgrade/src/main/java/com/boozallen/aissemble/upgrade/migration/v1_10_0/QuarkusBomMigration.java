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
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.technologybrewery.baton.util.pom.PomHelper;
import org.technologybrewery.baton.util.pom.PomModifications;

import com.boozallen.aissemble.upgrade.migration.AbstractPomMigration;

import static org.technologybrewery.baton.util.pom.LocationAwareMavenReader.END;
import static org.technologybrewery.baton.util.pom.LocationAwareMavenReader.START;

/**
 * Updates all references to the quarkus-bom and quarkus-universe-bom to use the new
 * aissemble-quarkus-bom for managing Quarkus dependencies
 */
public class QuarkusBomMigration extends AbstractPomMigration {
    private static final Logger logger = LoggerFactory.getLogger(QuarkusBomMigration.class);
    private static final String AISSEMBLE_GROUP_ID = "com.boozallen.aissemble";
    private static final String AISSEMBLE_QUARKUS_BOM_ARTIFACT_ID = "aissemble-quarkus-bom";
    private static final String AISSEMBLE_VERSION = "${version.aissemble}";

    @Override
    protected boolean shouldExecuteOnFile(File pomFile) {
        Model model = PomHelper.getLocationAnnotatedModel(pomFile);
        return !this.getMatchingDependenciesForProject(model, QuarkusBomMigration::isQuarkusBom).isEmpty();
    }

    @Override
    protected boolean performMigration(File pomFile) {
        logger.info("Migrating file to aiSSEMBLE Quarkus BOM: {}", pomFile.getAbsolutePath());
        Model model = PomHelper.getLocationAnnotatedModel(pomFile);
        List<Dependency> quarkusBomDependencies = this.getMatchingDependenciesForProject(model, QuarkusBomMigration::isQuarkusBom);
        PomModifications modifications = new PomModifications();

        for (Dependency dependency : quarkusBomDependencies) {

            // Update the group and artifact ID
            modifications.add(replaceInTag(dependency, "groupId", AISSEMBLE_GROUP_ID));
            modifications.add(replaceInTag(dependency, "artifactId", AISSEMBLE_QUARKUS_BOM_ARTIFACT_ID));

            // Update/add the version
            if (dependency.getLocation("version") != null) {
                modifications.add(replaceInTag(dependency, "version", AISSEMBLE_VERSION));
            } else {
                int indentSize = dependency.getLocation("artifactId" + START).getColumnNumber() - 1;
                // assumes spaces instead of tabs, but accounting for tabs would be more trouble than it's worth IMO
                String indent = StringUtils.repeat(' ', indentSize);
                modifications.add(new PomModifications.Insertion(dependency.getLocation(END), 0,
                        ignore -> indent + "<version>" + AISSEMBLE_VERSION + "</version>\n"));
            }
        }
        if (!modifications.isEmpty()) {
            PomHelper.writeModifications(pomFile, modifications.finalizeMods());
        }
        return true;
    }

    /**
     * Determines whether the given dependency is of type quarkus-bom or quarkus-universe-bom
     */
    private static boolean isQuarkusBom(Dependency dependency) {
        return dependency.getGroupId().equals("io.quarkus") && 
            (dependency.getArtifactId().equals("quarkus-bom") || dependency.getArtifactId().equals("quarkus-universe-bom"));
    }
    
}
