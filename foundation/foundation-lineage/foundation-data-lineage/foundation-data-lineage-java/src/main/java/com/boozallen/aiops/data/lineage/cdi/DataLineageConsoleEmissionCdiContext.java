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

import com.boozallen.aiops.data.lineage.transport.ConsoleTransport;

import java.util.ArrayList;
import java.util.List;

/**
 * Adds necessary classes for Data Lineage event emission, with Console logging.
 */
public class DataLineageConsoleEmissionCdiContext extends DataLineageCdiContext {
    @Override
    public List<Class<?>> getCdiClasses() {
        List<Class<?>> beans = new ArrayList<>(super.getCdiClasses());

        beans.add(ConsoleTransport.class);

        return beans;
    }
}
