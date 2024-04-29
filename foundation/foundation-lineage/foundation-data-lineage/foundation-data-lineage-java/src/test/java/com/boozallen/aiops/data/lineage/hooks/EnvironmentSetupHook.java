package com.boozallen.aiops.data.lineage.hooks;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Data Lineage Java
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import io.cucumber.java.BeforeAll;

public class EnvironmentSetupHook {

    @BeforeAll
    public static void setup() {
        System.setProperty("KRAUSENING_BASE", "target/test-classes/krausening/base");
    }
}
