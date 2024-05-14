package com.boozallen.aissemble.upgrade.migration.v1_7_0;

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
import com.boozallen.aissemble.upgrade.util.pom.PomModifications;
import org.apache.maven.model.Build;
import org.apache.maven.model.InputLocation;
import org.apache.maven.model.Model;

import java.io.File;

import static com.boozallen.aissemble.upgrade.util.pom.PomHelper.getLocationAnnotatedModel;
import static com.boozallen.aissemble.upgrade.util.pom.PomHelper.writeModifications;


/**
 * This migration adds a new build directory tag to the build node of the project's pom.xml file, ensuring that Habushu
 * modules leverage their dist folder for their output rather than target.  This approach aligns more closely to typical
 * python conventions, and ensures that the build cache can find and replace those artifacts via the default config.
 */
public class EnableDistOutputFolderForHabushuBuildCacheMigration extends AbstractAissembleMigration {
    private boolean isHabushuPackaged(Model model) {
        return "habushu".equalsIgnoreCase(model.getPackaging());
    }

    private boolean doesBuildDirTagExist(Model model) {
        return null != model.getBuild().getLocation("directory");
    }

    @Override
    protected boolean shouldExecuteOnFile(File file) {
        Model model = getLocationAnnotatedModel(file);
        return !doesBuildDirTagExist(model) && isHabushuPackaged(model);
    }

    @Override
    protected boolean performMigration(File file) {
        Model model = getLocationAnnotatedModel(file);
        Build build = model.getBuild();
        InputLocation buildLocation = build.getLocation("");
        InputLocation inputLocation = new InputLocation(buildLocation.getLineNumber() + 1, buildLocation.getColumnNumber());
        String line = "\t\t<directory>dist</directory>\n";
        int indent = 2;
        PomModifications modifications = new PomModifications();
        modifications.add(new PomModifications.Insertion(inputLocation, indent, l -> line));
        return writeModifications(file, modifications.finalizeMods());
    }
}
