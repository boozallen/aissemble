package com.boozallen.drift.detection.algorithm;

/*-
 * #%L
 * Drift Detection::Core
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class CalculateConfigurationsFromControlSteps {

    private StandardDeviationDriftAlgorithm algorithm = new StandardDeviationDriftAlgorithm();

    private double[] values;

    private Double mean;

    private Double standardDeviation;

    private Double roundedValue;

    @Given("a set of control data {string}")
    public void a_set_of_control_data(String text) {
        values = parseText(text);
    }

    @Given("a calculated mean of {double}")
    public void a_calculated_mean_of(Double mean) {
        this.mean = mean;
    }

    @When("the mean is calculated")
    public void the_mean_is_calculated() {
        mean = algorithm.calculateMean(values);
    }

    @When("the standard deviation is calculated")
    public void the_standard_deviation_is_calculated() {
        standardDeviation = algorithm.calculateStandardDeviation(mean, values);
    }

    @When("a calculated number {double} is rounded")
    public void a_calculated_number_is_rounded(Double value) {
        roundedValue = algorithm.round(value);
    }

    @Then("the mean is {double}")
    public void the_mean_is(Double expectedMean) {
        assertNotNull("Actual mean was unexpectedly null", mean);
        assertEquals("Expected mean did not match actual", expectedMean, mean);
    }

    @Then("the standard deviation is {double}")
    public void the_standard_deviation_is(Double expectedStandardDeviation) {
        assertNotNull("Actual standard deviation was unexpectedly null", standardDeviation);
        assertEquals("Expected standard deviation did not match actual", expectedStandardDeviation, standardDeviation);
    }

    @Then("the number is rounded {double} using the HALF EVEN rounding method and last {int} digits")
    public void the_number_is_rounded_using_the_half_up_rounding_method_and_last_digits(Double expectedValue,
            Integer scale) {
        assertEquals("Rounded value did not match expected", expectedValue, roundedValue);
        BigDecimal bigDecimal = new BigDecimal(Double.toString(roundedValue));
        assertTrue("Value was not rounded", bigDecimal.scale() <= scale);
    }

    private double[] parseText(String text) {
        String[] parsedString = text.split(",");
        int length = parsedString.length;
        double[] values = new double[length];
        for (int i = 0; i < length; i++) {
            values[i] = Double.parseDouble(parsedString[i]);
        }
        return values;
    }

}
