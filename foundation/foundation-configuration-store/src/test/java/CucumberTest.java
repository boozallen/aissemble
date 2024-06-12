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
        tags = "not @manual")
public class CucumberTest extends CucumberQuarkusTest {
    public static void main(String[] args) {
        runMain(CucumberTest.class, args);
    }
}

