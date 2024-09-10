package com.boozallen.aissemble.upgrade.migration.v1_8_0;

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
import org.apache.maven.model.Build;
import org.apache.maven.model.BuildBase;
import org.apache.maven.model.InputLocation;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.technologybrewery.baton.util.pom.PomHelper;
import org.technologybrewery.baton.util.pom.PomModifications;
import org.technologybrewery.baton.util.pom.PomModifications.Insertion;

import java.io.File;

import static com.boozallen.aissemble.upgrade.migration.v1_8_0.OrphedomosToFabric8Migration.FABRIC8_ARTIFACT_ID;
import static com.boozallen.aissemble.upgrade.migration.v1_8_0.OrphedomosToFabric8Migration.FABRIC8_GROUP_ID;
import static com.boozallen.aissemble.upgrade.migration.v1_8_0.OrphedomosToFabric8Migration.FABRIC8_PACKAGING;
import static org.apache.commons.lang3.StringUtils.repeat;
import static org.technologybrewery.baton.util.pom.LocationAwareMavenReader.END;

/**
 * In the move from Orphedomos to Fabric8, we can no longer specify the Docker plugin as part of the build for a module
 * that is not a `docker-build` packaging type.  (Orphedomos allowed this and simply skipped all goals if the packaging
 * type was not `orphedomos`.)  This migration ensures all `docker-build` modules explicitly add the Docker plugin in
 * their build since this can no longer be inherited from a parent.
 */
public class AddFabric8ToDockerMigration extends AbstractPomMigration {

    @Override
    protected boolean shouldExecuteOnFile(File file) {
        Model model = PomHelper.getLocationAnnotatedModel(file);
        String packaging = model.getPackaging();
        if (FABRIC8_PACKAGING.equals(packaging)) {
            return getDockerPluginIfPresent(model.getBuild()) == null;
        } else {
            return false;
        }
    }

    @Override
    protected boolean performMigration(File file) {
        detectAndSetIndent(file);
        Model model = PomHelper.getLocationAnnotatedModel(file);
        Insertion insertion;
        Build build = model.getBuild();
        if (build == null) {
            insertion = new Insertion(model.getLocation(END), 0, this::getBuildDeclaration);
        } else if (build.getLocation("plugins") == null) {
            insertion = new Insertion(build.getLocation(END), 1, this::getPluginsDeclaration);
        } else {
            InputLocation start = build.getLocation("plugins" + END);
            insertion = new Insertion(start, 2, this::getPluginDeclaration);
        }
        PomModifications modifications = new PomModifications();
        modifications.add(insertion);
        modifications.finalizeMods();
        return PomHelper.writeModifications(file, modifications.finalizeMods());
    }

    private String getBuildDeclaration(String ignore) {
        return repeat(indent, 1) + "<build>\n" +
                getPluginsDeclaration(ignore) +
                repeat(indent, 1) + "</build>\n";
    }
    private String getPluginsDeclaration(String ignore) {
        return repeat(indent, 2) + "<plugins>\n" +
                getPluginDeclaration(ignore) +
                repeat(indent, 2) + "</plugins>\n";
    }
    private String getPluginDeclaration(String ignore) {
        return repeat(indent, 3) + "<plugin>\n" +
                repeat(indent, 4) + "<groupId>" + FABRIC8_GROUP_ID + "</groupId>\n" +
                repeat(indent, 4) + "<artifactId>" + FABRIC8_ARTIFACT_ID + "</artifactId>\n" +
                repeat(indent, 3) + "</plugin>\n";
    }

    private static Plugin getDockerPluginIfPresent(BuildBase build) {
        Plugin dockerPlugin = null;
        if (build != null && build.getPlugins() != null) {
            for (Plugin plugin : build.getPlugins()) {
                if (FABRIC8_ARTIFACT_ID.equals(plugin.getArtifactId())) {
                    dockerPlugin = plugin;
                    break;
                }
            }
        }
        return dockerPlugin;
    }
}
