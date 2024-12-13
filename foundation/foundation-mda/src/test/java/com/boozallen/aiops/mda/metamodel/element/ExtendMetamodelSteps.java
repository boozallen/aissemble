package com.boozallen.aiops.mda.metamodel.element;

/*-
 * #%L
 * aiSSEMBLE::Foundation::MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.technologybrewery.fermenter.mda.GenerateSourcesHelper;
import org.technologybrewery.fermenter.mda.element.ExpandedProfile;
import org.technologybrewery.fermenter.mda.metamodel.ModelInstanceRepositoryManager;
import org.technologybrewery.fermenter.mda.metamodel.ModelInstanceUrl;
import org.technologybrewery.fermenter.mda.metamodel.ModelRepositoryConfiguration;
import org.technologybrewery.fermenter.mda.util.JsonUtils;

import com.boozallen.aiops.mda.metamodel.AissembleModelInstanceRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class ExtendMetamodelSteps extends AbstractModelInstanceSteps {

    private static final Logger logger = LoggerFactory.getLogger(ExtendMetamodelSteps.class);
    private Exception exception;

    @Before("@extend-metamodel")
    public void setUp(Scenario scenario) throws Exception {
        this.exception = null;
        this.scenario = scenario.getName();
        FileUtils.deleteDirectory(GENERATED_METADATA_DIRECTORY);
    }
    
    @Given("a class extending the {string} metamodel with new simple and complex fields")
    public void a_class_extending_the_metamodel_with_new_simple_and_complex_fields(String metamodelType) throws Exception {
        switch (metamodelType) {
            case "dictionary":
                this.createDictionaryExtension();
                break;
            case "record":
                this.createRecordExtension();
                break;
            case "pipeline":
                this.createPipelineExtension();
                break;
            case "composite":
                this.createCompositeExtension();
                break;
        }
    }

    @When("the {string} profile is generated")
    public void the_profile_is_generated(String profile) {
        readMetadata();

        // Skip the composite case as there is not a generation profile for it
        if (!profile.isBlank()) {
            try {
                Map<String, ExpandedProfile> profiles = loadProfiles();
                GenerateSourcesHelper.performSourceGeneration(profile, profiles, this::createGenerationContext, (missingProfile, foundProfiles) -> {
                    throw new RuntimeException("Missing profile: " + missingProfile);
                }, new Slf4jDelegate(logger), projectDir.toFile());
            } catch (Exception exception) {
                this.exception = exception;
            }
        }
    }

    @Then("the generation completes successfully")
    public void the_generation_completes_successfully() {
        assertNull("An exception occurred with reading the metamodel/source generation", this.exception);
    }

    @Then("the new {string} metamodel fields can be accessed")
    public void the_new_metamodel_fields_can_be_accessed(String metamodelType) {
        switch (metamodelType) {
            case "dictionary":
                Dictionary dictionary = this.metadataRepo.getDictionary("TestDictionaryWithTypeList");
                DictionaryTypeElementExtension dictionaryTypeElementExtension = (DictionaryTypeElementExtension) dictionary.getDictionaryTypes().get(0);
                this.validateExtension(metamodelType, dictionaryTypeElementExtension.simpleField, dictionaryTypeElementExtension.person);
                break;
            case "record":
                Record record = this.metadataRepo.getRecord("TestRecordWithFieldList");
                RecordFieldElementExtension recordFieldElementExtension = (RecordFieldElementExtension) record.getFields().get(0);
                this.validateExtension(metamodelType, recordFieldElementExtension.simpleField, recordFieldElementExtension.person);
                break;
            case "pipeline":
                PipelineTypeElementExtension pipelineTypeElementExtension = (PipelineTypeElementExtension) this.metadataRepo.getPipeline("TestPipeline").getType();
                this.validateExtension(metamodelType, pipelineTypeElementExtension.simpleField, pipelineTypeElementExtension.person);
                break;
            case "composite":
                CompositeFieldElementExtension compositeFieldElementExtension = (CompositeFieldElementExtension) this.metadataRepo.getComposite("TestCompositeWithFieldList").getFields().get(0);
                this.validateExtension(metamodelType, compositeFieldElementExtension.simpleField, compositeFieldElementExtension.person);
                break;
        }
    }

    /*
     * Create a basic dictionary using the custom extension class
     */
    private void createDictionaryExtension() throws Exception {
        this.createProject("test-dictionary-extension", "shared");

        DictionaryTypeElementExtension dictionaryTypeElementExtension = new DictionaryTypeElementExtension();
        dictionaryTypeElementExtension.setName("CustomDictionaryExtension");
        dictionaryTypeElementExtension.setSimpleType("string");
        dictionaryTypeElementExtension.simpleField = "TestSimpleField";
        dictionaryTypeElementExtension.person = new Person("Test Person", 50);

        this.createSampleDictionary(Arrays.asList(dictionaryTypeElementExtension));
    }

    /*
     * Create a basic record using the custom extension class with a dictionary for the record to reference as its Field Type.
     */
    private void createRecordExtension() throws Exception {
        this.createProject("test-record-extension", "shared");

        DictionaryTypeElement dictionaryTypeElement = new DictionaryTypeElement();
        dictionaryTypeElement.setName("StringField");
        dictionaryTypeElement.setSimpleType("string");

        this.createSampleDictionary(Arrays.asList(dictionaryTypeElement));

        RecordFieldTypeElement recordFieldTypeElement = new RecordFieldTypeElement();
        recordFieldTypeElement.setName("StringField");
        recordFieldTypeElement.setPackage(BOOZ_ALLEN_PACKAGE);

        RecordFieldElementExtension recordFieldElementExtension = new RecordFieldElementExtension();
        recordFieldElementExtension.setName("CustomRecordExtension");
        recordFieldElementExtension.setType(recordFieldTypeElement);
        recordFieldElementExtension.simpleField = "TestSimpleField";
        recordFieldElementExtension.person = new Person("Test Person", 50);

        this.createSampleRecord(Arrays.asList(recordFieldElementExtension));
    }

    /*
     * Create a basic pipeline using the custom extension class
     */
    private void createPipelineExtension() throws Exception {
        this.createProject("test-pipeline-extension", "pipelines");

        PipelineTypeElementExtension pipelineTypeElementExtension = new PipelineTypeElementExtension();
        pipelineTypeElementExtension.setName("data-flow");
        pipelineTypeElementExtension.setImplementation("data-delivery-spark");
        pipelineTypeElementExtension.simpleField = "TestSimpleField";
        pipelineTypeElementExtension.person = new Person("Test Person", 50);

        PipelineElement pipeline = new PipelineElement();
        pipeline.setName("TestPipeline");
        pipeline.setPackage(BOOZ_ALLEN_PACKAGE);
        pipeline.setType(pipelineTypeElementExtension);
        
        this.savePipelineToFile(pipeline);
    }

    /*
     * Create a basic composite using the custom extension class with a dictionary for the composite to reference as its type.
     */
    private void createCompositeExtension() throws Exception {
        this.createProject("test-composite-extension", "shared");

        DictionaryTypeElement dictionaryTypeElement = new DictionaryTypeElement();
        dictionaryTypeElement.setName("StringField");
        dictionaryTypeElement.setSimpleType("string");

        CompositeFieldElementExtension compositeFieldElementExtension = new CompositeFieldElementExtension();
        compositeFieldElementExtension.setName("CustomCompositeExtension");
        compositeFieldElementExtension.setType(dictionaryTypeElement);
        compositeFieldElementExtension.simpleField = "TestSimpleField";
        compositeFieldElementExtension.person = new Person("Test Person", 50);

        this.createSampleComposite(Arrays.asList(compositeFieldElementExtension));
    }

    /*
     * Validate the extension within the metamodel repo contains the expected new values
     */
    private void validateExtension(String type, String simpleFieldValue, Person complexField) {
        assertEquals("The " + type + "should have the new simple field with the correct value", simpleFieldValue, "TestSimpleField");
        assertEquals("The " + type + "should have the new complex field with the correct name value", complexField.name, "Test Person");
        assertEquals("The " + type + "should have the new complex field with the correct age value", complexField.age, 50);
    }

    public static class Person {
        public String name;
        public int age;

        public Person() {}
        
        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }
    }

    public static class DictionaryTypeElementExtension extends DictionaryTypeElement {
        public String simpleField;
        public Person person;
    }

    public static class RecordFieldElementExtension extends RecordFieldElement {
        public String simpleField;
        public Person person;
    }

    public static class PipelineTypeElementExtension extends PipelineTypeElement {
        public String simpleField;
        public Person person;
    }

    public static class CompositeFieldElementExtension extends CompositeFieldElement {
        public String simpleField;
        public Person person;
    }

    /*
     * Class extending the aissemble metamodel repository with our custom classes
     */
    class ExtensionModelInstanceRepository extends AissembleModelInstanceRepository {
        public ExtensionModelInstanceRepository(ModelRepositoryConfiguration config) {
            super(config);
            this.configureCustomObjectMapper();
        }

        public void configureCustomObjectMapper() {
            SimpleModule module = new SimpleModule();

            // Add custom Pipeline extension
            module.addAbstractTypeMapping(PipelineType.class, PipelineTypeElementExtension.class);

            // Add custom dictionary extension
            module.addAbstractTypeMapping(DictionaryType.class, DictionaryTypeElementExtension.class);

            // Add custom record extension
            module.addAbstractTypeMapping(RecordField.class, RecordFieldElementExtension.class);
            module.addAbstractTypeMapping(RecordFieldType.class, RecordFieldTypeElement.class);

            // Add custom composite extension
            module.addAbstractTypeMapping(CompositeField.class, CompositeFieldElementExtension.class);

            ObjectMapper localMapper = new ObjectMapper();
            localMapper.registerModule(module);
            JsonUtils.setObjectMapper(localMapper);
        }
    }

    /*
     * Override to use our custom Model Instance Repository
     */
    @Override
    protected void readMetadata(String artifactId) {
        // reduce debugging output by ensuring expected directories exist:
        dictionariesDirectory.mkdirs();
        compositesDirectory.mkdirs();
        recordsDirectory.mkdirs();
        pipelinesDirectory.mkdirs();

        ModelRepositoryConfiguration config = new ModelRepositoryConfiguration();
        config.setArtifactId(artifactId);
        config.setBasePackage(BOOZ_ALLEN_PACKAGE);
        Map<String, ModelInstanceUrl> metadataUrlMap = config.getMetamodelInstanceLocations();
        metadataUrlMap.put(artifactId,
                new ModelInstanceUrl(artifactId, recordsDirectory.getParentFile().toURI().toString()));

        List<String> targetedInstances = config.getTargetModelInstances();
        targetedInstances.add(artifactId);

        metadataRepo = new ExtensionModelInstanceRepository(config);
        ModelInstanceRepositoryManager.setRepository(metadataRepo);
        metadataRepo.load();
        metadataRepo.validate();
    }
}
