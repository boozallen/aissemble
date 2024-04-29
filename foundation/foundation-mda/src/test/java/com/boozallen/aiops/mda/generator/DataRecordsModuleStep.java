package com.boozallen.aiops.mda.generator;/*-
 * #%L
 * aiSSEMBLE::Foundation::MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */
import com.boozallen.aiops.mda.metamodel.element.AbstractModelInstanceSteps;
import com.boozallen.aiops.mda.metamodel.element.BasePipelineDecorator;
import com.boozallen.aiops.mda.metamodel.element.Pipeline;
import com.boozallen.aiops.mda.metamodel.element.RecordElement;
import com.boozallen.aiops.mda.metamodel.element.python.PythonRecord;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.commons.io.FileUtils;
import org.apache.maven.model.Model;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.technologybrewery.fermenter.mda.GenerateSourcesHelper;
import org.technologybrewery.fermenter.mda.element.ExpandedProfile;
import org.technologybrewery.fermenter.mda.notification.Notification;
import org.technologybrewery.fermenter.mda.notification.NotificationCollector;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static org.junit.Assert.assertTrue;

public class DataRecordsModuleStep extends AbstractModelInstanceSteps {
    private static final Logger logger = LoggerFactory.getLogger(DataRecordsModuleStep.class);
    private RecordElement record;

    @Before("@data-records-generation")
    public void setup(Scenario scenario) throws IOException {
        this.scenario = scenario.getName();
        FileUtils.deleteDirectory(GENERATED_METADATA_DIRECTORY);
    }


    @Given("a project named {string}")
    public void a_project_named(String projectName) throws IOException {
        createProject(projectName, "shared");
    }

    @Given("a {string} pipeline using {string}")
    public void a_pipeline_using(String typeName, String implName) throws IOException {
        a_pipeline_using_named(typeName, implName, unique("TestPipeline"));
    }

    @Given("a {string} pipeline using {string} named {string}")
    public void a_pipeline_using_named(String typeName, String implName, String name) throws IOException {
        createPipeline(name, typeName, implName);
    }

    @Given("a dictionary and 0 or more record models")
    public void a_dictionary_and_0_or_more_record_models() throws Exception {
        createSampleDictionaryType();
        record = new RecordElement();
        record.setName("TestRecord");
        record.setPackage(BOOZ_ALLEN_PACKAGE);
        saveRecordToFile(record);
    }

    @Given("a pre-existing semantic data module called {string} with {string}")
    public void a_pre_existing_semantic_data_module_called_with(String moduleName, String packaging) throws IOException {
        Model shared = new Model();
        shared.setArtifactId(projectName + "-shared");
        shared.setPackaging("pom");
        shared.setModules(List.of(moduleName));
        Path sharedPath = writeChildPom(shared).getParent();

        Model model = new Model();
        model.setArtifactId(moduleName);
        model.setPackaging(packaging);
        writeChildPom(model, sharedPath);
    }

    @When("the profile {string} is generated")
    public void the_profile_is_generated(String profileName) throws Exception {
        readMetadata(projectName);
        Map<String, ExpandedProfile> profiles = loadProfiles();
        GenerateSourcesHelper.performSourceGeneration(profileName, profiles,
                this::createGenerationContext,
                (missingProfile, foundProfiles) -> {
                    throw new RuntimeException("Missing profile: " + missingProfile);
                },
                new Slf4jDelegate(logger),
                projectDir.toFile());
    }

    @Then("a semantic-data module is generated with {string} under {string}")
    public void a_semantic_data_module_with_generated_in_under(String expectedPackaging, String moduleLocation) throws Exception {
        Path pom = projectDir.resolve(moduleLocation).resolve("pom.xml");
        assertModulePackaging(pom, expectedPackaging);

    }

    @Then("a module with Spark functionality is generated with {string} under {string}")
    public void a_module_with_spark_functionality_with_generated_in_under(String expectedPackaging, String moduleLocation) throws Exception {
        Path pom = projectDir.resolve(moduleLocation).resolve("pom.xml");
        assertModulePackaging(pom, expectedPackaging);
    }

    @Then("the {string} module generates the profile {string}")
    public void the_module_generates_the_profile(String module, String profile) throws Exception {
        Path pom = projectDir.resolve(module).resolve("pom.xml");
        boolean hasProfile = queryPom(pom, "/project/build/plugins/plugin[artifactId='fermenter-mda']/configuration/profile", profile);
        assertTrue("Fermenter profile missing from [" + module + "]: " + profile, hasProfile);
    }

    @Then("the user is notified that the module {string} must be added to the parent POM")
    public void the_user_is_notified_that_the_module_must_be_added_to_the_parent_pom(String moduleName) {
        String moduleItem = "<module>" + moduleName + "</module>";
        String file = projectDir.resolve("pom.xml").toString();
        Notification moduleNotification = getNotification(file, "module");
        assertTrue("Module notification missing item " + moduleItem, moduleNotification.getItems().contains(moduleItem));
    }

    @Then("no module is generated under {string}")
    public void no_module_is_generated_under(String moduleName) {
        Path module = projectDir.resolve(moduleName);
        assertTrue("Language-specific module created when existing module should have been reused: " + moduleName,
                Files.notExists(module));
    }

    @Then("the pipeline POM has a dependency on {string}")
    public void the_pipeline_pom_has_a_dependency_on(String dataModule) throws Exception {
        the_pipeline_pom_has_a_dependency_on(pipeline.getName(), dataModule);
    }

    @Then("{string} has a dependency on {string}")
    public void has_a_dependency_on(String module, String dependency) throws Exception {
        Path pom = projectDir.resolve(module).resolve("pom.xml");
        assertTrue("File not created: " + pom, Files.exists(pom) && Files.isRegularFile(pom));
        boolean hasDependency = queryPom(pom, "/project/dependencies/dependency/artifactId", dependency);
        assertTrue("Dependency " + dependency + " not found in " + pom, hasDependency);
    }

    @Then("the {string} pipeline POM has a dependency on {string}")
    public void the_pipeline_pom_has_a_dependency_on(String pipelineName, String dataModule) throws Exception {
        Pipeline pipeline = pipelines.get(pipelineName);
        BasePipelineDecorator decoratedPipeline = new BasePipelineDecorator(pipeline);
        has_a_dependency_on(decoratedPipeline.deriveArtifactIdFromCamelCase(), dataModule);
    }

    @Then("the pyproject.toml file has a dependency on {string}")
    public void the_pyproject_toml_file_has_a_dependency_on(String dataModule) throws IOException {
        Path pyproject = projectDir.resolve("pyproject.toml");
        Pattern monoRepoPattern = Pattern.compile(dataModule + " *= *\\{path *= \".*?" + dataModule + "\".*?}");
        boolean hasDependency = Files.lines(pyproject)
                .anyMatch(monoRepoPattern.asMatchPredicate());
        assertTrue("Dependency " + dataModule + " not found in " + pyproject, hasDependency);
    }

    @Then("the core semantic-data classes are generated in the module")
    public void the_core_semantic_data_classes_are_generated_in_the_module() {
        Path recordClass = getRecordClass(false);
        assertTrue("Record class not created: " + recordClass, Files.exists(recordClass));
    }

    @Then("the Spark functionality is generated in the module")
    public void the_spark_functionality_is_generated_in_the_module() {
        Path schemaClass = getRecordClass(true);
        assertTrue("Schema class not created: " + schemaClass, Files.exists(schemaClass));
    }

    private Path getRecordClass(boolean schema) {
        String classFileName;
        Path packagePath = projectDir.resolve("main");
        if (Files.exists(packagePath.resolve("java"))) {
            classFileName = record.getName() + (schema ? "Schema" : "") + ".java";
            packagePath = packagePath.resolve(Path.of("java", BOOZ_ALLEN_PACKAGE.split("\\.")));
        } else {
            classFileName = new PythonRecord(record).getSnakeCaseName() + (schema ? "_schema" : "") + ".py";
            packagePath = packagePath.resolve(schema ? "schema" : "record");
        }
        return packagePath.resolve(classFileName);
    }

    private Path writeProjectPom(Model model) throws IOException {
        return writePom(model, projectDir);
    }

    private Path writeChildPom(Model model) throws IOException {
        return writeChildPom(model, projectDir);
    }

    private Path writeChildPom(Model model, Path parent) throws IOException {
        return writePom(model, parent.resolve(model.getArtifactId()));
    }

    private void assertModulePackaging(Path pom, String expectedPackaging) throws Exception {
        String pipelineImpl = pipeline.getType().getImplementation();
        assertTrue("Module not created for " + pipelineImpl + " @ " + pom, Files.exists(pom.getParent()));
        assertTrue("Module POM missing for " + pipelineImpl + " @ " + pom, Files.exists(pom));
        String packaging = getPomPackaging(pom);
        Assert.assertEquals("Module packaging incorrect " + pipelineImpl, expectedPackaging, packaging);
    }

    private String getPomPackaging(Path pom) throws Exception {
        NodeList nodeList = queryPom(pom, "/project/packaging");
        String packaging;
        if (nodeList.getLength() == 0) {
            packaging = "jar"; //default packaging
        } else {
            packaging = nodeList.item(0).getTextContent();
        }
        return packaging;
    }

    private Notification getNotification(String file, String notificationType) {
        Map<String, Map<String, Notification>> notifications = NotificationCollector.getNotifications();
        assertTrue("No notifications for file " + file, notifications.containsKey(file));
        Map<String, Notification> fileNotifications = notifications.get(file);
        assertTrue("No notifications of type " + notificationType + " for " + file,
                fileNotifications.containsKey(file + "_" + notificationType));
        return fileNotifications.get(file + "_" + notificationType);
    }

    /**
     * Query the POM for specific content. Finds all nodes that match the given query and checks if any of them have the
     * expected content.
     *
     * @param pom     the POM file
     * @param query    the XPath query
     * @param content the expected content
     * @return true if the content is found
     * @throws Exception if the POM cannot be read
     */
    private static boolean queryPom(Path pom, String query, String content) throws Exception {
        NodeList nodeList = queryPom(pom, query);
        boolean found = false;
        for (int i = 0; i < nodeList.getLength(); i++) {
            if (content.equals(nodeList.item(i).getTextContent())) {
                found = true;
                break;
            }
        }
        return found;
    }

    private static NodeList queryPom(Path pom, String query) throws Exception {
        NodeList nodeList;
        try (FileInputStream in = new FileInputStream(pom.toFile())) {
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Document xmlDocument = builder.parse(in);
            XPath xPath = XPathFactory.newInstance().newXPath();
            nodeList = (NodeList) xPath.compile(query).evaluate(xmlDocument, XPathConstants.NODESET);
        }
        return nodeList;
    }
}
