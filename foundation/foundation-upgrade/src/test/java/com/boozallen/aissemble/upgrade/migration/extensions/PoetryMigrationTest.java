package com.boozallen.aissemble.upgrade.migration.extensions;

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

import com.boozallen.aissemble.upgrade.migration.AbstractPoetryMigration;
import com.boozallen.aissemble.upgrade.util.PythonPackageMigration;

public class PoetryMigrationTest extends AbstractPoetryMigration {

    private final static Set<PythonPackageMigration> MIGRATION_PYTHON_PACKAGES = Set.of(
        new PythonPackageMigration("package_1", "new_package_1", "2.0.0"), 
        new PythonPackageMigration("package_2", "new_package_2", "2.0.0"), 
        new PythonPackageMigration("package_3", "new_package_3", "1.0.0")
    );

    @Override
    public Set<PythonPackageMigration> getPythonPackageMigrations() {
        return MIGRATION_PYTHON_PACKAGES;
    }
    
}
