package com.boozallen.aissemble.upgrade.migration;

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
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.technologybrewery.baton.BatonException;

import com.boozallen.aissemble.upgrade.util.FileUtils;
import com.boozallen.aissemble.upgrade.util.PythonPackageMigration;
import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.file.FileConfig;

/**
 * Baton migration used to migrate aiSSEMBLE python packages to the new naming convention and version in pyproject.toml files. 
 */
public abstract class AbstractPoetryMigration extends AbstractAissembleMigration {

    private static final Logger logger = LoggerFactory.getLogger(AbstractPoetryMigration.class);
    public static final String DEPENDENCIES_KEY = "tool.poetry.dependencies";

    @Override
    protected boolean shouldExecuteOnFile(File file) {
        boolean shouldExecute = false;
        FileConfig poetryConfig = FileConfig.of(file);
        poetryConfig.load();
        Optional<Config> dependenciesOpt = poetryConfig.getOptional(DEPENDENCIES_KEY);
        if(dependenciesOpt.isPresent()) {
            Config dependencies = dependenciesOpt.get();
            shouldExecute = hasOldPackages(dependencies);
        }
        else {
            throw new BatonException(String.format("Could not get dependencies for file %s", file.getAbsolutePath()));
        }
        return shouldExecute;
    }

    private boolean hasOldPackages(Config currentPackages) {
        boolean hasOldPackages = false;
        for(PythonPackageMigration packageMigration : getPythonPackageMigrations()) {
            if(currentPackages.contains(packageMigration.getOldName())) {
                hasOldPackages = true;
                break;
            }
        }
        return hasOldPackages;
    }

    @Override
    protected boolean performMigration(File file) {
        boolean performedSuccessfully = false;
        FileConfig pyproject = FileConfig.of(file);
        pyproject.load();
        Config dependencies = pyproject.get(DEPENDENCIES_KEY);
        try {
            performedSuccessfully = migrateOldPythonPackages(dependencies, file);
        } catch (IOException e) {
            throw new BatonException("Could not migrate file: " + file.getPath(), e);
        }
        return performedSuccessfully;
    }

    private boolean migrateOldPythonPackages(Config config, File pyproject) throws IOException {
        boolean success = false;
        for(PythonPackageMigration packageMigration : getPythonPackageMigrations()) {
            String oldPackageName = packageMigration.getOldName();
            if(config.getOptional(oldPackageName).isPresent()) {
                // Old package is preset in pyproject.toml
                String regex = String.format("%s = \".+\"", packageMigration.getOldName());
                String replacement = String.format("%s = \"%s\"", packageMigration.getNewName(), packageMigration.getNewVersion());
                FileUtils.replaceInFile(pyproject, regex, replacement);
                logger.info("Replacing Python package {} -> {} = \"{}\"", 
                    packageMigration.getOldName(), packageMigration.getNewName(), packageMigration.getNewVersion());
                success = true;
            }
        }
        return success;
    }

    public abstract Set<PythonPackageMigration> getPythonPackageMigrations();
    
}
