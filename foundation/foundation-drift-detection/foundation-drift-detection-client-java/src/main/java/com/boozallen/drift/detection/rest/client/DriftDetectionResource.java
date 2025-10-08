package com.boozallen.drift.detection.rest.client;

/*-
 * #%L
 * Drift Detection::Rest Client
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

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

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
