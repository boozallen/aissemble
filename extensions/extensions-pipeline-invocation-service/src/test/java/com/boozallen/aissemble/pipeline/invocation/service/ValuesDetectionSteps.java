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

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@ApplicationScoped
public class ValuesDetectionSteps {
    @Inject
    ValuesFileRegistry registry;

    @When("the application initializes")
    public void the_application_initializes() {}

    @When("spark application values files are present for detection")
    public void spark_application_values_files_are_present_for_detection() {}

    @Then("the values index will be populated")
    public void the_values_index_will_be_populated() {
        assertFalse(registry.getAvailableSparkApplications().isEmpty());
    }

    @Then("each present spark application will be detected")
    public void each_present_spark_application_will_be_detected() {
        List<String> apps = List.of("java-pipeline", "python-pipeline");
        apps.forEach(name -> assertTrue(registry.getAvailableSparkApplications().contains(name)));
    }

    @Then("each spark application's classifier set will be detected")
    public void each_spark_applications_classifier_set_will_be_detected() {
        assertTrue(registry.getValuesCollection("java-pipeline")
                .getAvailableClassifiers()
                .containsAll(Set.of("base", "ci", "debug", "dev")));

        assertTrue(registry.getValuesCollection("python-pipeline")
                .getAvailableClassifiers()
                .containsAll(Set.of("base", "ci", "dev")));
    }
}
