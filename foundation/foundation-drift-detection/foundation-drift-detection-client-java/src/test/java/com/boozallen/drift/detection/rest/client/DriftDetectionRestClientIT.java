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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;

import com.boozallen.drift.detection.DriftDetectionResult;
import com.boozallen.drift.detection.data.DriftData;
import com.boozallen.drift.detection.util.TestDataUtil;
import com.boozallen.aissemble.alerting.core.Alert.Status;

import io.quarkus.test.junit.QuarkusTest;

/**
 * Very similar test to {@link DriftDetectionService} but this one uses the rest
 * client and the service running in the container.
 * 
 * @author Booz Allen Hamilton
 *
 */
@QuarkusTest
public class DriftDetectionRestClientIT {

    /**
     * Until we can get docker up and running during the maven build as part of
     * the test, this is tied to the configurations in
     * drift-detection-policies.json in the docker module.
     */
    private static final String POLICY_IDENTIFIER = "ExamplePolicy";

    private DriftDetectionResult result;

    @Inject
    DriftDetectionResource driftDetectionResource;

    @Test
    public void testDriftInvocationEndpointWithMultipleDriftVariables() {
        a_policy_has_been_defined_for_detecting_drift();
        i_invoke_drift_specifying_the_policy_using_the_rest_service_with_multiple_drift_variables();
        i_receive_the_results_of_drift_detection();
    }

    @Test
    public void testDriftInvocationEndpointWithASingleDriftVariable() {
        a_policy_has_been_defined_for_detecting_drift();
        i_invoke_drift_specifying_the_policy_using_the_rest_service_with_a_single_drift_variable();
        i_receive_the_results_of_drift_detection();
    }

    public void a_policy_has_been_defined_for_detecting_drift() {
        // Happens server side on startup and reload
    }

    public void i_invoke_drift_specifying_the_policy_using_the_rest_service_with_multiple_drift_variables() {
        DriftData driftData = TestDataUtil.createRandomDriftVariables(40, 60);
        invokeDrift(driftData);
    }

    public void i_invoke_drift_specifying_the_policy_using_the_rest_service_with_a_single_drift_variable() {
        DriftData driftData = TestDataUtil.createRandomDriftVariable(40, 60);
        invokeDrift(driftData);
    }

    public void i_receive_the_results_of_drift_detection() {
        assertNotNull(result, "The drift detection result was unexpectedly null");
        assertEquals(Status.SUCCESS, result.getStatus(), "Drift was unexpectedly unencountered");
    }

    private void invokeDrift(DriftData driftData) {
        result = driftDetectionResource.detect(POLICY_IDENTIFIER, driftData);
    }

}
