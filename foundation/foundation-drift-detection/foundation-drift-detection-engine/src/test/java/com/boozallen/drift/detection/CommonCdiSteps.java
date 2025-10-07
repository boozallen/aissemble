package com.boozallen.drift.detection;

/*-
 * #%L
 * Drift Detection::Core
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

import com.boozallen.aissemble.alerting.core.cdi.AlertingCdiContext;
import com.boozallen.aissemble.core.cdi.CdiContainer;
import com.boozallen.drift.detection.cdi.TestCdiContext;
import com.boozallen.drift.detection.consumer.TestConsumer;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import org.jboss.weld.environment.se.WeldContainer;

import java.util.List;

public class CommonCdiSteps {

    private WeldContainer container;

    @Before("@cdi")
    public void setUp() {
        container = CdiContainer.create(List.of(new TestCdiContext(), new AlertingCdiContext()));
    }

    @After("@cdi")
    public void tearDown() {
        if (container != null) {
            container.close();
            container = null;
        }
        TestConsumer.getAllAlerts().clear();
    }

}
