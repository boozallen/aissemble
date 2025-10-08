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

import com.boozallen.aissemble.alerting.core.cdi.AlertingCdiContainer;
import com.boozallen.aissemble.quarkus.context.QuarkusCdiContext;

/**
 * {@link TeamsCdiContainer} creates the cdi configurations needed to use the
 * Teams Alerts.
 * 
 * @author Booz Allen Hamilton
 *
 */
public class TeamsCdiContainer extends AlertingCdiContainer {

    public TeamsCdiContainer() {
        super();
        create(new TeamsCdiContext());
        create(new QuarkusCdiContext());
    }

}
