package com.boozallen.aiops.mda.metamodel.element;

/*-
 * #%L
 * AIOps Foundation::AIOps MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
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
