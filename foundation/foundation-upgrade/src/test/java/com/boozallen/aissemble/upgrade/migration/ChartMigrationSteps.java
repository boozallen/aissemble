package com.boozallen.aissemble.upgrade.migration;/*-
 * #%L
 * aiSSEMBLE::Foundation::Upgrade
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aissemble.upgrade.migration.extensions.HelmChartsMigrationTestBase;
import com.boozallen.aissemble.upgrade.migration.extensions.HelmChartsMigrationTestRelease;
import com.boozallen.aissemble.upgrade.migration.extensions.HelmChartsMigrationTestSnapshot;
import com.boozallen.aissemble.upgrade.migration.extensions.HelmChartsV1MigrationTest;
import com.boozallen.aissemble.upgrade.migration.utils.MigrationTestUtils;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.commons.lang.math.RandomUtils;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class ChartMigrationSteps {

    private static final boolean CHART_HAS_DEPENDENCIES = true;
    private final String TEST_HELMCHARTS_DIRECTORY_NAME = "./target/test-classes/migration/helmcharts/";
    private final String DEFAULT_TEST_PREFIX = "aissemble-";
    private static final String NO_DEPENDENCIES_PREFIX = "no-dependencies-";
    private final String VERSION_ONE = "1.0.0";
    private final File testHelmChartsDirectory = new File(TEST_HELMCHARTS_DIRECTORY_NAME);
    private String chartNamePrefix;
    private File chartDirectory;
    private File chartFile;
    private File valuesFile;
    private boolean shouldExecute = false;
    private boolean executedSuccessfully = false;
    private boolean hasHostName = true;
    private boolean hasRepositoryInsteadOfImageName = false;
    private boolean containsOtherPropertiesAfterImageName = false;
    private String previousVersion;
    private HelmChartsMigrationTestBase migration;
    private HelmChartsV1MigrationTest migrationV1 = new HelmChartsV1MigrationTest();

    /**
     * Step used by multiple features to create Helm Chart test repo
     * @see specifications/chart-migration-v2.feature
     * @see specifications/chart-migration-v1.feature
    * */
    @Given("a helm chart with an aiSSEMBLE dependency")
    public void aHelmChartWithAnAiSSEMBLEDependency() {
        createYamlTestDirectory(DEFAULT_TEST_PREFIX);
    }


    @Given("a helm chart with a subchart that does NOT have a name prefixed with {string}")
    public void aHelmChartWithASubchartThatDoesNOTHaveANamePrefixedWith(String arg0) {
        createYamlTestDirectory("unassociated-");
    }

    @Given("a v1 chart")
    public void aV1Chart(List<String> expectedV1Charts) {
        // Choose a v1 chart name at random
        String chartName = expectedV1Charts.get(RandomUtils.nextInt(expectedV1Charts.size()-1));

        // Create the main chart directory
        createYamlTestDirectory(chartName);
    }

    @Given("the image tag in the values file is less than the current version of aiSSEMBLE")
    public void theImageTagInTheValuesFileIsLessThanTheCurrentVersionOfAiSSEMBLE() {
        createTestValuesFileInV1Format(migrationV1.getOutdatedAissembleVersion(), chartNamePrefix);
    }

    @Given("the image tag in the values file is equal to the current version of aiSSEMBLE")
    public void theImageTagInTheValuesFileIsEqualToTheCurrentVersionOfAiSSEMBLE() {
        createTestValuesFileInV1Format(migrationV1.getCurrentAissembleVersion(), chartNamePrefix);
    }

    @Given("the image tag in the values file is greater than the current version of aiSSEMBLE")
    public void theImageTagInTheValuesFileIsGreaterThanTheCurrentVersionOfAiSSEMBLE() {
        createTestValuesFileInV1Format(migrationV1.getNewerAissembleVersion(), chartNamePrefix);
    }

    @Given("the values file does not contain a hostname")
    public void theValuesFileDoesNotContainAHostname() {
        hasHostName = false;
    }

    @Given("the values file contains more properties after the image tag")
    public void theValuesFileContainsMorePropertiesAfterTheImageTag() {
        containsOtherPropertiesAfterImageName = true;
    }

    @Given("the values file does not contain a hostname or image name")
    public void theValuesFileDoesNotContainAHostnameOrImageName() {
        hasHostName = false;
        hasRepositoryInsteadOfImageName = true;
    }

    @Given("the dependency is on a version less than the current version of aiSSEMBLE")
    public void theDependencyIsOnAVersionLessThanTheCurrentVersionOfAiSSEMBLE() {
        migration = new HelmChartsMigrationTestRelease();
        previousVersion = migration.getOutdatedAissembleVersion();
        createTestChartFileInV2Format(previousVersion, !chartNamePrefix.equals(NO_DEPENDENCIES_PREFIX));
    }

    @Given("the dependency is on a version equal to the current version of aiSSEMBLE")
    public void theDependencyIsOnAVersionEqualToTheCurrentVersionOfAiSSEMBLE() {
        migration = new HelmChartsMigrationTestRelease();
        previousVersion = migration.getCurrentAissembleVersion();
        createTestChartFileInV2Format(previousVersion);

    }

    @Given("the dependency is on a version greater than the current version of aiSSEMBLE")
    public void theDependencyIsOnAVersionGreaterThanTheCurrentVersionOfAiSSEMBLE() {
        migration = new HelmChartsMigrationTestRelease();
        previousVersion = migration.getNewerAissembleVersion();
        createTestChartFileInV2Format(previousVersion);
    }

    @Given("the dependency is on a version less than the current snapshot version of aiSSEMBLE")
    public void theDependencyIsOnAVersionLessThanTheCurrentSnapshotVersionOfAiSSEMBLE() {
        migration = new HelmChartsMigrationTestSnapshot();
        previousVersion= migration.getOutdatedAissembleVersion();
        createTestChartFileInV2Format(previousVersion);
    }

    @Given("a helm chart with no aiSSEMBLE dependencies")
    public void aHelmChartWithNoAiSSEMBLEDependencies() {
        chartNamePrefix = NO_DEPENDENCIES_PREFIX;
        createYamlTestDirectory(chartNamePrefix);
    }

    @When("the helm chart migration executes")
    public void theHelmChartMigrationExecutes() {
        shouldExecute = migration.shouldExecuteOnFile(chartFile);
        executedSuccessfully = shouldExecute && migration.performMigration(chartFile);
    }

    @When("the v1 helm chart migration executes")
    public void theV1HelmChartMigrationExecutes() {
        shouldExecute = migrationV1.shouldExecuteOnFile(valuesFile);
        executedSuccessfully = shouldExecute && migrationV1.performMigration(valuesFile);
    }

    @Then("the version of the dependency is updated to the current version")
    public void theVersionOfTheDependencyIsUpdatedToTheCurrentVersion() {
        assertTrue("Chart file was not accepted as legible for aiSSEMBLE version upgrade", shouldExecute);
        assertTrue("Chart file was accepted for upgrade but not executed successfully", executedSuccessfully);
        assertTrue("Chart file contents empty", chartFile.length() > 0);

        HashMap<String, Object> chartFileContents;
            try {
                chartFileContents = MigrationTestUtils.extractYamlContents(chartFile);
                Object dependenciesObject = chartFileContents.get("dependencies");
                assertTrue(
                        "Unable to populate test chart file contents" ,
                        dependenciesObject instanceof List && !((List<?>) dependenciesObject).isEmpty()
                );

                String incomingAppVersion = (String) chartFileContents.get("appVersion");
                assertEquals("appVersion was changed when only dependency versions should have", VERSION_ONE, incomingAppVersion );
                String incomingVersion = (String) chartFileContents.get("version");
                assertEquals("Chart version was changed when only dependency versions should have", VERSION_ONE, incomingVersion );

                ArrayList<HashMap<String, Object>> dependencies = (ArrayList<HashMap<String, Object>>) dependenciesObject;
                String currentVersion = migration.getCurrentAissembleVersion();

                boolean aissembleTestDependencyExists = false;
                boolean nonAissembleTestDependencyExists = false;
                for (HashMap<String, Object> dependency : dependencies) {
                    Object incomingDependencyVersion = dependency.get("version");
                    String name = (String) dependency.get("name");
                    assertTrue("Unexpected type for version or version is null", incomingDependencyVersion instanceof String);

                    if (name.startsWith("aissemble-")) {
                        assertEquals(String.format("Helm chart file failed to migrate correctly for %s", name),
                                currentVersion,
                                incomingDependencyVersion
                        );
                        aissembleTestDependencyExists = true;
                    } else {
                        assertEquals(String.format("Helm chart file failed to migrate correctly for %s", name),
                                previousVersion,
                                incomingDependencyVersion
                        );
                        nonAissembleTestDependencyExists = true;
                    }

                }
                boolean noDependenciesWerePruned = aissembleTestDependencyExists && nonAissembleTestDependencyExists;
                assertTrue("Dependencies are missing from the output Chart file!", noDependenciesWerePruned);

            } catch (IOException e) {
                fail(String.format("Unable to parse yaml contents due to exception: %s", e));
            }
    }

    @Then("the chart file aiSSEMBLE upgrade is skipped")
    public void theChartFileAiSSEMBLEUpgradeIsSkipped() {
        assertFalse("Yaml marked for change when it is invalid candidate", shouldExecute);
        assertFalse("Yaml replacement executed even though versions matched", executedSuccessfully);
    }

    @Then("the version of the chart is updated to the current version in the values configuration")
    public void theVersionOfTheChartIsUpdatedToTheCurrentVersionInTheValuesConfiguration() {
        assertTrue("Values file was not accepted as legible for aiSSEMBLE version upgrade", shouldExecute);
        assertTrue("Values file was accepted for upgrade but not executed successfully", executedSuccessfully);
        assertTrue("Values file contents empty", valuesFile.length() > 0);

        HashMap<String, Object> valuesFileContents;
        try {
            valuesFileContents = MigrationTestUtils.extractYamlContents(valuesFile);
            HashMap<String,Object> image = (HashMap<String, Object>) valuesFileContents.get("image");
            String incomingVersionTag = (String) image.get("tag");

            assertEquals("Helm chart file failed to migrate correctly",
                    migrationV1.getCurrentAissembleVersion(),
                    incomingVersionTag
            );
        } catch (IOException e ) {
            fail(String.format("Unable to parse yaml contents due to exception: %s", e));
        }
    }

    private void createTestChartFileInV2Format(String version) {
        createTestChartFileInV2Format(version, CHART_HAS_DEPENDENCIES);
    }

    private void createTestChartFileInV2Format(String version, boolean hasDependencies) {
        Map<String, Object> testYamlContents = getTestV2ChartContents(version, hasDependencies);

        chartFile = new File(chartDirectory, "Chart.yaml");

        try {
            boolean helmFileCreated = chartFile.createNewFile();
            if (helmFileCreated) {
                Yaml yaml = getPrettyYaml();
                PrintWriter writer = new PrintWriter(chartFile);
                yaml.dump(testYamlContents, writer);
                assertTrue("Failed to populate test yaml", chartFile.length() > 0);
            } else {
                fail("Failed to create test yaml");
            }
        } catch (Exception e) {
            fail(String.format("encountered exception creating test yaml: %s", e.getMessage()));
        }
    }

    private void createTestValuesFileInV1Format(String version, String chartName) {
        final String imageName = String.format("somecoolresource/%s", chartName);
        Map<String, Object> testYamlContents = getTestV1ValuesContents(
                version,
                imageName,
                hasHostName ? chartName : null
        );

        valuesFile = new File(chartDirectory, "values.yaml");

        try {
            boolean valuesFileCreated = valuesFile.createNewFile();
            if (valuesFileCreated) {
                Yaml yaml = getPrettyYaml();
                PrintWriter writer = new PrintWriter(valuesFile);
                yaml.dump(testYamlContents, writer);
                assertTrue("Failed to populate test values yaml", valuesFile.length() > 0);
            } else {
                fail("Failed to create test values yaml");
            }
        } catch (Exception e) {
            fail(String.format("encountered exception creating test values yaml: %s", e.getMessage()));
        }
    }

    private static Yaml getPrettyYaml() {
        DumperOptions format = new DumperOptions();
        format.setPrettyFlow(true);
        format.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Yaml yaml = new Yaml(format);
        return yaml;
    }

    private Map<String, Object> getTestV2ChartContents(String version, boolean hasDependencies) {
        String name = String.format("%stest", chartNamePrefix);
        Map<String, Object> testYaml = new HashMap<>();
        testYaml.put("apiVersion", "v2");
        testYaml.put("description", "Test yaml");
        testYaml.put("name", name);
        testYaml.put("appVersion", VERSION_ONE); //aissemble version
        testYaml.put("version", VERSION_ONE); // helm file version - update this every time it's changed

        if (hasDependencies) {
            List<Object> dependencies = new ArrayList<>();

            Map<String, Object> aissembleDependency = new HashMap<>();
            aissembleDependency.put("name", "aissemble-dependency");
            aissembleDependency.put("version", version);
            aissembleDependency.put("repository", "file://somethinsomethin/");
            dependencies.add(aissembleDependency);

            Map<String, Object> nonAissembleDependency = new HashMap<>();
            nonAissembleDependency.put("name", "dependency");
            nonAissembleDependency.put("version", version);
            nonAissembleDependency.put("repository", "file://somethinsomethin/");
            dependencies.add(nonAissembleDependency);

            testYaml.put("dependencies", dependencies);
        }

        return testYaml;
    }

    private Map<String,Object> getTestV1ValuesContents(String version, String imageName, String hostname) {
        Map<String, Object> testYaml = new HashMap<>();

        if (hostname != null) {
            testYaml.put("hostname", hostname);
        }

        if (containsOtherPropertiesAfterImageName) {
            testYaml.put("image", Map.of(
                    "tag", version,
                    "name", imageName,
                    "test1", version,
                    "test2", imageName,
                    "test23", imageName
            ));

        } else if (hasRepositoryInsteadOfImageName) {
            testYaml.put("image", Map.of(
                    "tag", version,
                    "repository", String.format("docker-internal.nexus.testfoo.com/test/testfoo/%s",imageName),
                    "name", imageName
            ));
        } else {
            testYaml.put("image", Map.of(
                    "tag", version,
                    "name", imageName
            ));
        }

        return testYaml;
    }
    private void createYamlTestDirectory(String prefix) {
        chartNamePrefix = prefix;
        String chartDirectoryName = String.format("%stest-chart", chartNamePrefix);
        chartDirectory = new File(testHelmChartsDirectory, chartDirectoryName);

        int iterator = 1;
        while (chartDirectory.exists()) {
            String newPath = String.format("%s/%s-%d", testHelmChartsDirectory.getPath(), chartDirectoryName, iterator);
            chartDirectory = new File(newPath);
            ++iterator;
        }

        boolean directoriesCreated = chartDirectory.mkdirs();
        assertTrue("Failed to create test directory for test Yamls", directoriesCreated);
    }
}
