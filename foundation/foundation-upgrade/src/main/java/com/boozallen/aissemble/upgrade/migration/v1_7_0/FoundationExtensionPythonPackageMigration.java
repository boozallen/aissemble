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

import java.util.Set;
import java.util.HashSet;

import com.boozallen.aissemble.upgrade.migration.AbstractPoetryMigration;
import com.boozallen.aissemble.upgrade.util.PythonPackageMigration;

/**
 * Baton migration used to migration foundation and extension Python package in to new naming convention in pyproject.toml files
 */
public class FoundationExtensionPythonPackageMigration extends AbstractPoetryMigration {
    
    private static final Set<String> OLD_PYTHON_PACKAGES = Set.of(
        "foundation-core-python", 
        "foundation-pdp-client-python", 
        "foundation-model-lineage", 
        "foundation-encryption-policy-python", 
        "extensions-encryption-vault-python", 
        "extensions-data-delivery-spark-py", 
        "foundation-data-lineage-python"
    );
    private static final String PACKAGE_PREFIX = "aissemble-";

    @Override
    public Set<PythonPackageMigration> getPythonPackageMigrations() {
        Set<PythonPackageMigration> packageMigrations = new HashSet<>();
        String aissembleVersion = getAissembleVersion().replaceAll("-SNAPSHOT", ".*");
        for(String oldPackageName : OLD_PYTHON_PACKAGES) {
            String newPackageName = String.format("%s%s", PACKAGE_PREFIX, oldPackageName);
            packageMigrations.add(new PythonPackageMigration(oldPackageName, newPackageName, aissembleVersion));
        }
        return packageMigrations;
    }
    
}
