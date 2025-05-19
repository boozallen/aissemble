package com.boozallen.aissemble.upgrade.migration.version_specific;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Upgrade
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aissemble.upgrade.migration.AbstractMigrationTest;
import com.boozallen.aissemble.upgrade.migration.extensions.RuffTomlFileGenerationMigrationTest;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.Assert.assertTrue;

public class RuffTomlFileGenerationMigrationStep extends AbstractMigrationTest {

    @Given("a projects does not have a root ruff.toml file")
    public void a_projects_does_not_have_a_root_ruff_toml_file() {
        setTestFileToVersionMigration("RuffTomlfileGenerationMigration", "pom.xml");
    }

    @When("the ruff.toml generation migration is performed")
    public void the_ruff_toml_generation_migration_is_performed() throws XmlPullParserException, IOException {
        performMigration(new RuffTomlFileGenerationMigrationTest(testFile));
    }

    @Then("the ruff.toml is generated")
    public void the_ruff_toml_is_generated() throws IOException {
        File ruffTomlFile = new File(testFile.getParentFile(), RuffTomlFileGenerationMigration.RUFF_TOML_FILE);
        assertTrue("ruff.toml was not generated.", ruffTomlFile.exists());
    }
}
