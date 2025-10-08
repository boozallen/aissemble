package com.boozallen.aiops.mda.metamodel.element;

/*-
 * #%L
 * AIOps Foundation::AIOps MDA
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

import com.boozallen.aiops.mda.ManualActionNotificationService;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.inject.Inject;

/**
 * Represents a data profiling instance.
 */
@JsonPropertyOrder({ "enabled" })
public class DataProfilingElement extends AbstractEnabledElement implements DataProfiling {

    @Inject
    @JsonIgnore
    private ManualActionNotificationService manualActionNotificationService;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object featureDiscovery;

    public void setFeatureDiscovery(Object featureDiscovery) {
        this.featureDiscovery = featureDiscovery;
    }

    @Override
    public void validate() {
        if (this.featureDiscovery != null) {
            manualActionNotificationService.addSchemaElementDeprecationNotice("featureDiscovery", "Pipeline/step/data-profiling");
        }
    }
}
