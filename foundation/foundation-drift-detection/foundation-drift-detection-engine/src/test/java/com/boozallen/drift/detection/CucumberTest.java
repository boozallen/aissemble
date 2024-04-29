package com.boozallen.drift.detection;

/*-
 * #%L
 * Drift Detection::Core
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

/**
 * {@link CucumberTest} boot straps the cucumber steps.
 * 
 * @author Booz Allen Hamilton
 *
 */
@RunWith(Cucumber.class)
@CucumberOptions(features = "src/test/resources/specifications", plugin = {
        "json:target/cucumber-reports/cucumber.json" }, tags = "not @manual")
public class CucumberTest {

}
