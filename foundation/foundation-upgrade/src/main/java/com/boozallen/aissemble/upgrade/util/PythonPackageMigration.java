package com.boozallen.aissemble.upgrade.util;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Upgrade
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */


public class PythonPackageMigration {

    private final String oldName;
    private final String newName;
    private final String newVersion;

    public PythonPackageMigration(String oldName, String newName, String newVersion) {
        this.oldName = oldName;
        this.newName = newName;
        this.newVersion = newVersion;
    }

    public String getOldName() {
        return this.oldName;
    }

    public String getNewName() {
        return this.newName;
    }

    public String getNewVersion() {
        return this.newVersion;
    }
    
}
