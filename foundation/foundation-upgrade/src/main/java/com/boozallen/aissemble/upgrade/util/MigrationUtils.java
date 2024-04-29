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

import com.vdurmont.semver4j.Semver;

public class MigrationUtils {
    /**
     * Checks if version1 is less than version2 using the Semver4j library.
     *
     * @param version1
     * @param version2
     * @return isLessThanVersion - if true, version1 is less than version2.
     */
    public static boolean isLessThanVersion(String version1, String version2) {
        Semver sem = new Semver(version1);
        return sem.isLowerThan(version2);
    }
}
