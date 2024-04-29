package com.boozallen.aiops.data.lineage.cdi;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Data Lineage Java
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aiops.data.lineage.test.transport.TestConsoleTransport;

import java.util.ArrayList;
import java.util.List;

public class TestDataLineageConsoleEmissionCdiContext extends DataLineageCdiContext {
    @Override
    public List<Class<?>> getCdiClasses() {
        List<Class<?>> beans = new ArrayList<>(super.getCdiClasses());
        beans.add(TestConsoleTransport.class);
        return beans;
    }
}
