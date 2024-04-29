package com.boozallen.drift.detection.rest.client;

/*-
 * #%L
 * Drift Detection::Rest Client
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.boozallen.drift.detection.DriftDetectionResult;
import com.boozallen.drift.detection.data.DriftData;
import com.boozallen.drift.detection.data.DriftDataInput;

@ApplicationScoped
public class DriftDetectionResource {
    
    @Inject
    @RestClient
    DriftDetectionClient client;

    public DriftDetectionResult detect(String policyIdentifier) {
       return client.invoke(policyIdentifier, null);
    }
    
    public DriftDetectionResult detect(String policyIdentifier, DriftData input) {
        DriftDataInput wrappedData = wrapInput(input, null);
        return client.invoke(policyIdentifier, wrappedData);
    }
    
    public DriftDetectionResult detect(String policyIdentifier, DriftData input, DriftData control) {
        DriftDataInput wrappedData = wrapInput(input, control);
        return client.invoke(policyIdentifier, wrappedData);
    }
    
    private DriftDataInput wrapInput(DriftData input, DriftData control) {
        DriftDataInput wrappedData = new DriftDataInput();
        wrappedData.setInput(input);
        wrappedData.setControl(control);
        return wrappedData;
    }



}
