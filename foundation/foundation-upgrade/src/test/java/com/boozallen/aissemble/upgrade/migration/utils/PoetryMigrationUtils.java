package com.boozallen.aissemble.upgrade.migration.utils;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Upgrade
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

 import static org.junit.Assert.assertEquals;

 import java.io.File;
 import java.nio.file.Paths;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 import java.util.Optional;
 import java.util.Set;
 
 import com.boozallen.aissemble.upgrade.migration.AbstractPoetryMigration;
 import com.boozallen.aissemble.upgrade.util.PythonPackageMigration;
 import com.electronwill.nightconfig.core.Config;
 import com.electronwill.nightconfig.core.file.FileConfig;
 

public class PoetryMigrationUtils {

    public static final String PYPROJECT_FILE = Paths.get("pyproject", "pyproject.toml").toString();
    
    public static void verifyPoetryMigration(Set<PythonPackageMigration> packageMigrations, File testFile) {
        Map<String, String> expectedPackageResults = getNewPackages(packageMigrations);
        FileConfig pyproject = FileConfig.of(testFile);
        pyproject.load();
        Optional<Config> dependenciesOpt = pyproject.getOptional(AbstractPoetryMigration.DEPENDENCIES_KEY);
        if(dependenciesOpt.isPresent()) {
            Config config = dependenciesOpt.get();
            for(String expectedPackageName : expectedPackageResults.keySet()) {
                String version = config.get(expectedPackageName);
                String expectedVersion = expectedPackageResults.get(expectedPackageName);
                assertEquals(version, expectedVersion);
            }
        }
    }

    public static void createPyproject(Map<String, String> pythonPackages, File testFile) {
        FileConfig pyproject = FileConfig.of(testFile);
        Config config = Config.inMemory();
        for(String pythonPackage : pythonPackages.keySet()) {
            config.add(pythonPackage, pythonPackages.get(pythonPackage));
        }
        pyproject.add(AbstractPoetryMigration.DEPENDENCIES_KEY, config);
        pyproject.save();
        pyproject.close();
    }

    public static Map<String, String> getOldPackages(Set<PythonPackageMigration> packageMigrations) {
        Map<String, String> oldPackages = new HashMap<>();
        for(PythonPackageMigration packageMigration : packageMigrations) {
            oldPackages.put(packageMigration.getOldName(), "8.0.0");
        }
        return oldPackages;
    }


    public static Map<String, String> getNewPackages(Set<PythonPackageMigration> packageMigrations) {
        Map<String, String> newPackages = new HashMap<>();
        for(PythonPackageMigration packageMigration : packageMigrations) {
            newPackages.put(packageMigration.getNewName(), packageMigration.getNewVersion());
        }
        return newPackages;
    }

    public static Map<String, String> getPartialOldPythonPackages(Set<PythonPackageMigration> packageMigrations) {
        Map<String, String> partialPythonPackages = new HashMap<>();
        List<PythonPackageMigration> pythonPackages = new ArrayList<>(packageMigrations);
        for(int i=0;i<pythonPackages.size();i++) {
            PythonPackageMigration packageMigration = pythonPackages.get(i);
            if(i % 2 == 0) {
                // If even, add new package
                partialPythonPackages.put(packageMigration.getNewName(), packageMigration.getNewVersion());
            }
            else {
                // Add an old package
                partialPythonPackages.put(packageMigration.getOldName(), "3.0.0");
            }
        }
        return partialPythonPackages;
    }
}
