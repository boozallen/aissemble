package com.boozallen.aissemble.upgrade.migration.version_specific;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Upgrade
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
        setTestFileToVersionMigration("RuffTomlFileGenerationMigration", "pom.xml");
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
