package com.boozallen.aissemble.alerting.teams.cdi;

/*-
 * #%L
 * aiSSEMBLE::Extensions::Alerting::Teams
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import java.util.ArrayList;
import java.util.List;

import jakarta.enterprise.inject.spi.Extension;

import com.boozallen.aissemble.alerting.teams.TeamsClient;
import com.boozallen.aissemble.alerting.teams.TeamsMessageService;
import com.boozallen.aissemble.core.cdi.CdiContext;

public class TeamsCdiContext implements CdiContext {

    @Override
    public List<Class<?>> getCdiClasses() {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        classes.add(TeamsMessageService.class);
        classes.add(TeamsClient.class);
        return classes;
    }

    @Override
    public List<Extension> getExtensions() {
        return null;
    }

}
