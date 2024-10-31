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

import com.boozallen.aissemble.upgrade.migration.AbstractPomMigration;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.technologybrewery.baton.BatonException;
import org.technologybrewery.baton.util.FileUtils;
import org.technologybrewery.baton.util.pom.PomHelper;
import org.technologybrewery.baton.util.pom.PomModifications;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.technologybrewery.baton.util.pom.LocationAwareMavenReader.END;
import static org.technologybrewery.baton.util.pom.LocationAwareMavenReader.START;

/**
 * To work with Spark 3.5, the Delta Lake dependencies need to be updated to 3.2.1.  The current dependencies are 2.4.0.
 * Though the version itself should be managed via the `${version.delta}` property in build-parent, the upgrade from 2.x
 * to 3.x also involves a rename of the delta-core_[SCALA-VER] dependency to delta-spark_[SCALA_VER]. This migration
 * renames this dependency, and ensures that the version is being pulled from build-parent.
 */
public class DeltaSparkPomMigration extends AbstractPomMigration {

    public static final String ARTIFACT_ID = "artifactId";
    public static final String DELTA_VERSION = "${version.delta}";

    @Override
    protected boolean shouldExecuteOnFile(File file) {
        try {
            return FileUtils.hasRegExMatch("<artifactId>[\\n\\s]*delta-core_2.1[23][\\n\\s]*</artifactId>", file);
        } catch (IOException e) {
            throw new BatonException("Could not check file for DeltaLake dependencies: " + file.getPath(), e);
        }
    }

    @Override
    protected boolean performMigration(File file) {
        Model model = PomHelper.getLocationAnnotatedModel(file);
        List<Dependency> deltaCoreDependencies = this.getMatchingDependenciesForProject(model, DeltaSparkPomMigration::isDeltaCore);
        PomModifications modifications = new PomModifications();
        for (Dependency dependency : deltaCoreDependencies) {
            String artifactId = dependency.getArtifactId();
            String newArtifact = artifactId.replace("core", "spark");
            modifications.add(replaceInTag(dependency, ARTIFACT_ID, newArtifact));
            if (dependency.getLocation("version") != null) {
                modifications.add(replaceInTag(dependency, "version", DELTA_VERSION));
            } else {
                int indentSize = dependency.getLocation(ARTIFACT_ID + START).getColumnNumber() - 1;
                // assumes spaces instead of tabs, but accounting for tabs would be more trouble than it's worth IMO
                String indent = StringUtils.repeat(' ', indentSize);
                modifications.add(new PomModifications.Insertion(dependency.getLocation(END), 0,
                        ignore -> indent + "<version>" + DELTA_VERSION + "</version>\n"));
            }
        }
        if (!modifications.isEmpty()) {
            PomHelper.writeModifications(file, modifications.finalizeMods());
        }
        return true;
    }

    private static boolean isDeltaCore(Dependency dep) {
        return dep.getGroupId().equals("io.delta") && dep.getArtifactId().startsWith("delta-core_");
    }
}
