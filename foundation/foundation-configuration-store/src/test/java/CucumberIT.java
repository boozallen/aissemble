/*-
 * #%L
 * aiSSEMBLE::Foundation::Configuration::Store
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import io.quarkiverse.cucumber.CucumberOptions;

import io.quarkiverse.cucumber.CucumberQuarkusTest;

@CucumberOptions(
        features = "src/test/resources/specifications",
        plugin = {"json:target/cucumber-reports/cucumber.json"},
        tags = "@integration")
public class CucumberIT extends CucumberQuarkusTest {
    public static void main(String[] args) {
        runMain(CucumberIT.class, args);
    }
}

