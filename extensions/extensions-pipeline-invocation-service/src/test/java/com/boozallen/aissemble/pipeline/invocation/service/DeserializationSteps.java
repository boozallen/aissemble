package com.boozallen.aissemble.pipeline.invocation.service;

/*-
 * #%L
 * aiSSEMBLE::Extensions::Pipeline Invocation Service
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

import com.boozallen.aissemble.pipeline.invocation.service.serialization.PipelineInvocationRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import jakarta.enterprise.context.ApplicationScoped;

import static org.junit.jupiter.api.Assertions.*;

@ApplicationScoped
public class DeserializationSteps {
    private String inputString;
    @When("an appropriately formatted string is provided")
    public void an_appropriately_formatted_string_is_provided() {
        inputString =
                "{" +
                    "\"applicationName\": \"python-pipeline\", " +
                    "\"profile\": \"ci\", " +
                    "\"overrideValues\": {" +
                        "\"metadata.name\": \"testapp\"" +
                    "}" +
                "}";
    }

    @Then("the string can be deserialized to an object")
    public void the_string_can_be_deserialized_to_an_object() throws JsonProcessingException {
        PipelineInvocationRequest req = PipelineInvocationRequest.fromString(inputString);
        assertEquals(req.getApplicationName(), "python-pipeline");
        assertEquals(ExecutionProfile.CI, req.getProfile());
        assertTrue(req.getOverrideValues().containsKey("metadata.name"));
        assertEquals("testapp", req.getOverrideValues().get("metadata.name"));
    }
}
