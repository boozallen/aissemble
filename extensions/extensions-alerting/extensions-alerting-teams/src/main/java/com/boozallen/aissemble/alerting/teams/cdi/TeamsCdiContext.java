package com.boozallen.aissemble.alerting.teams.cdi;

/*-
 * #%L
 * aiSSEMBLE::Extensions::Alerting::Teams
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
