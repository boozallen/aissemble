package com.boozallen.drift.detection.algorithm;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class CalculateBoundsSteps {

    private Double mean;

    private Double standardDeviation;

    private Double zScore;

    private Double actualUpperBound;

    private Double actualLowerBound;

    @Given("a mean of {double}, a standard deviation of {double}, and a zScore {double}")
    public void a_mean_of_a_standard_deviation_of_and_a_z_score(Double mean, Double standardDeviation, Double zScore) {
        this.mean = mean;
        this.standardDeviation = standardDeviation;
        this.zScore = zScore;
    }

    @When("the bounds for drift detection are calculated")
    public void the_bounds_for_drift_detection_are_calculated() {
        StandardDeviationDriftAlgorithm standardDeviationAlgorithm = new StandardDeviationDriftAlgorithm();
        actualUpperBound = standardDeviationAlgorithm.calculateUpperBound(mean, standardDeviation, zScore);
        actualLowerBound = standardDeviationAlgorithm.calculateLowerBound(mean, standardDeviation, zScore);
    }

    @Then("the upper bound is determined to be {double}")
    public void the_upper_bound_is_determined_to_be(Double expectedUpperBound) {
        assertNotNull("Upper bound was unexpectedly null", actualUpperBound);
        assertEquals("Actual upper bound did not match expected", expectedUpperBound, actualUpperBound);
    }

    @Then("the lower bound is determined to be {double}")
    public void the_lower_bound_is_determined_to_be(Double expectedLowerBound) {
        assertNotNull("Lower bound was unexpectedly null", actualLowerBound);
        assertEquals("Actual lower bound did not match expected", expectedLowerBound, actualLowerBound);
    }

}
