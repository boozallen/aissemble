package com.boozallen.aissemble.upgrade.migration.v1_7_0;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Upgrade
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aissemble.upgrade.migration.AbstractAissembleMigration;
import com.boozallen.aissemble.upgrade.migration.AbstractMigrationTest;
import com.boozallen.aissemble.upgrade.util.pom.PomHelper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.w3c.dom.Node;
import org.apache.maven.model.Model;

import static org.junit.Assert.*;

public class EnableDistOutputFolderForHabushuBuildCacheMigrationSteps extends AbstractMigrationTest {
    @Given("a habushu-packaged pom file where the build directory is unspecified")
    public void a_habushu_packaged_pom_file_where_the_build_directory_is_unspecified() {
        testFile = getTestFile("v1_7_0/EnableHabushuBuildCacheMigration/migration/add-dir-when-build-tag-exists/pom.xml");
    }

    @Given("a habushu-packaged pom file where the build block is unspecified")
    public void a_habushu_packaged_pom_file_where_the_build_block_is_unspecified() {
        testFile = getTestFile("v1_7_0/EnableHabushuBuildCacheMigration/migration/add-dir-and-build-when-build-tag-does-not-exist/pom.xml");
    }

    private AbstractAissembleMigration migration;

    @When("the habushu build cache migration executes")
    public void the_habushu_build_cache_migration_executes() {
        performMigration(new EnableDistOutputFolderForHabushuBuildCacheMigration());
    }

    @Then("the pom file contains a specification of the build directory")
    public void the_pom_file_contains_a_specification_of_the_build_directory() {
        assertTrue(shouldExecute);

        Model model = PomHelper.getLocationAnnotatedModel(testFile);
        assertNotNull(model.getBuild().getDirectory());
        assertEquals("dist", model.getBuild().getDirectory().toLowerCase());
    }

    @Given("a non-habushu-packaged pom file where the packaging is unspecified")
    public void a_non_habushu_packaged_pom_file_where_the_packaging_is_unspecified() {
        testFile = getTestFile("v1_7_0/EnableHabushuBuildCacheMigration/skip-migration/only-migrate-habushu-modules/pom.xml");
    }

    @Given("a pom file where the build directory is already specified")
    public void a_pom_file_where_the_build_directory_is_already_specified() {
        testFile = getTestFile("v1_7_0/EnableHabushuBuildCacheMigration/skip-migration/do-not-replace-existing-build-directory/pom.xml");
    }

    @Then("the pom file will not be marked for migration")
    public void the_pom_file_will_not_be_marked_for_migration() {
        assertFalse(shouldExecute);
    }
}
