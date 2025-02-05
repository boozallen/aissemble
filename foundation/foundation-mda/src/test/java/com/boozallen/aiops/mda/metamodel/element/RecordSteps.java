package com.boozallen.aiops.mda.metamodel.element;

/*-
 * #%L
 * AIOps Foundation::AIOps MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.boozallen.aiops.mda.generator.common.FrameworkEnum;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.technologybrewery.fermenter.mda.util.MessageTracker;
import org.technologybrewery.fermenter.mda.metamodel.ModelInstanceRepositoryManager;

import com.boozallen.aiops.mda.metamodel.json.AissembleMdaJsonUtils;
import com.google.common.collect.Maps;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class RecordSteps extends AbstractModelInstanceSteps {

    public static final String TEST_RECORD_RELATIONS = "test.record.relations";
    protected String recordPackageName;
    protected Record record;
    protected boolean encounteredError;

    @Before("@record")
    public void setUpObjectMapper() throws Exception {
        AissembleMdaJsonUtils.configureCustomObjectMappper();

        MessageTracker messageTracker = MessageTracker.getInstance();
        messageTracker.clear();

        FileUtils.deleteDirectory(GENERATED_METADATA_DIRECTORY);
    }

    @Given("the following dictionary types:")
    public void the_following_dictionary_types(List<DictionaryTypeElement> dictionaryTypes) throws Exception {
        createSampleDictionary(dictionaryTypes);
    }

    @Given("a record described by {string}, {string}")
    public void a_record_described_by(String name, String packageName) {
        RecordElement newRecord = createNewRecordWithNameAndPackage(name, packageName);
        saveRecordToFile(newRecord);
    }

    @Given("a record with a description {string}")
    public void a_record_with_a_description(String description) {
        RecordElement newRecord = createNewRecordWithNameAndPackage("DescriptionTest", BOOZ_ALLEN_PACKAGE);
        newRecord.setDescription(description);
        saveRecordToFile(newRecord);
    }

    @Given("a record with the fields named:")
    public void a_record_with_the_fields_named(List<String> fieldNames) {
        RecordElement newRecord = createNewRecordWithNameAndPackage("FieldTest", BOOZ_ALLEN_PACKAGE);
        for (String fieldName : fieldNames) {
            RecordFieldElement field = new RecordFieldElement();
            field.setName(fieldName);
            RecordFieldTypeElement type = createDefaultFieldType();
            field.setType(type);
            newRecord.addField(field);
        }
        saveRecordToFile(newRecord);
    }

    @Given("a record with a field that has a description {string}")
    public void a_record_with_a_field_that_has_a_description(String description) {
        RecordElement newRecord = createNewRecordWithNameAndPackage("FieldDescriptionTest", BOOZ_ALLEN_PACKAGE);
        RecordFieldElement field = createDefaultField("testFieldDescription");
        field.setDescription(description);
        newRecord.addField(field);
        saveRecordToFile(newRecord);
    }

    @Given("a record with a field that has dictionary type named {string}")
    public void a_record_with_a_field_that_has_dictionary_type_named(String dictionaryTypeName) {
        RecordElement newRecord = createNewRecordWithNameAndPackage("DictionaryTypeTest", BOOZ_ALLEN_PACKAGE);
        RecordFieldElement field = new RecordFieldElement();
        field.setName("testType" + StringUtils.capitalize(dictionaryTypeName));
        RecordFieldTypeElement type = new RecordFieldTypeElement();
        type.setPackage(BOOZ_ALLEN_PACKAGE);
        type.setName(dictionaryTypeName);
        field.setType(type);
        newRecord.addField(field);
        saveRecordToFile(newRecord);
    }

    @Given("a record with a field specifying the column name {string}")
    public void a_record_with_a_field_specifying_the_column_name(String columnName) {
        RecordElement newRecord = createNewRecordWithNameAndPackage("ColumnTest", BOOZ_ALLEN_PACKAGE);
        RecordFieldElement field = createDefaultField("testColumn" + StringUtils.capitalize(columnName));
        field.setColumn(columnName);
        newRecord.addField(field);
        saveRecordToFile(newRecord);
    }

    @Given("a record with a field specifying the column is required")
    public void a_record_with_a_field_specifying_the_column_is_required() {
        RecordElement newRecord = createNewRecordWithNameAndPackage("RequiredFieldTest", BOOZ_ALLEN_PACKAGE);
        RecordFieldElement field = createDefaultField("testRequiredField");
        field.setRequired(Boolean.TRUE);
        newRecord.addField(field);
        saveRecordToFile(newRecord);
    }

    @Given("a record with a field specifying the column as optional")
    public void a_record_with_a_field_specifying_the_column_as_optional() {
        RecordElement newRecord = createNewRecordWithNameAndPackage("OptionalFieldTest", BOOZ_ALLEN_PACKAGE);
        RecordFieldElement field = createDefaultField("testOptionalField");
        field.setRequired(Boolean.FALSE);
        newRecord.addField(field);
        saveRecordToFile(newRecord);
    }

    @Given("a record with a field that has dictionary type named {string} and a protection policy of {string}")
    public void a_record_with_a_field_that_has_dictionary_type_named_and_a_protection_policy_of(
            String dictionaryTypeName, String overrideProtectionPolicy) {

        RecordElement newRecord = createNewRecordWithNameAndPackage("ProtectionPolicyOverrideTest", BOOZ_ALLEN_PACKAGE);
        RecordFieldElement field = new RecordFieldElement();
        field.setName("testPolicyOverride");
        RecordFieldTypeElement type = new RecordFieldTypeElement();
        type.setPackage(BOOZ_ALLEN_PACKAGE);
        type.setName(dictionaryTypeName);
        field.setType(type);
        field.setProtectionPolicy(overrideProtectionPolicy);
        newRecord.addField(field);
        saveRecordToFile(newRecord);
    }

    @Given("a record with a field that has dictionary type named {string} and a ethics policy of {string}")
    public void a_record_with_a_field_that_has_dictionary_type_named_and_a_ethics_policy_of(String dictionaryTypeName,
            String overrideEthicsPolicy) {

        RecordElement newRecord = createNewRecordWithNameAndPackage("EthicsPolicyOverrideTest", BOOZ_ALLEN_PACKAGE);
        RecordFieldElement field = new RecordFieldElement();
        field.setName("testPolicyOverride");
        RecordFieldTypeElement type = new RecordFieldTypeElement();
        type.setPackage(BOOZ_ALLEN_PACKAGE);
        type.setName(dictionaryTypeName);
        field.setType(type);
        field.setEthicsPolicy(overrideEthicsPolicy);
        newRecord.addField(field);
        saveRecordToFile(newRecord);
    }

    @Given("a record with a field that has dictionary type named {string} and a drift policy of {string}")
    public void a_record_with_a_field_that_has_dictionary_type_named_and_a_drift_policy_of(String dictionaryTypeName,
            String overrideDriftPolicy) {

        RecordElement newRecord = createNewRecordWithNameAndPackage("DriftPolicyOverrideTest", BOOZ_ALLEN_PACKAGE);
        RecordFieldElement field = new RecordFieldElement();
        field.setName("testPolicyOverride");
        RecordFieldTypeElement type = new RecordFieldTypeElement();
        type.setPackage(BOOZ_ALLEN_PACKAGE);
        type.setName(dictionaryTypeName);
        field.setType(type);
        field.setDriftPolicy(overrideDriftPolicy);
        newRecord.addField(field);
        saveRecordToFile(newRecord);
    }

    @Given("a valid record with data access configuration")
    public void a_valid_record_with_data_access_configuration() {
        RecordElement newRecord = createNewRecordWithNameAndPackage("DataAccessEnabledTest", BOOZ_ALLEN_PACKAGE);
        DataAccessElement dataAccess = new DataAccessElement();
        newRecord.setDataAccess(dataAccess);
        saveRecordToFile(newRecord);
    }

    @Given("a valid record with data access disabled")
    public void a_valid_record_with_data_access_disabled() {
        RecordElement newRecord = createNewRecordWithNameAndPackage("DataAccessDisabledTest", BOOZ_ALLEN_PACKAGE);
        DataAccessElement dataAccess = new DataAccessElement();
        dataAccess.setEnabled(false);
        newRecord.setDataAccess(dataAccess);
        saveRecordToFile(newRecord);
    }

    @When("records are read")
    public void records_are_read() {
        readMetadata();
        Map<String, Record> records = metadataRepo.getRecords(recordPackageName);
        record = records.values().iterator().next();

        MessageTracker messageTracker = MessageTracker.getInstance();
        encounteredError = messageTracker.hasErrors();

    }

    @Then("a valid record is available can be looked up by the name {string} and {string}")
    public void a_valid_record_is_available_can_be_looked_up_by_the_name_and(String expectedName,
            String expectedPackageName) {

        assertNoErrorsEncountered();
        assertDictionaryFoundByNameAndPackage(expectedName, expectedPackageName);
    }

    @Then("a valid record is available with a description of {string}")
    public void a_valid_record_is_available_with_a_description_of(String expectedDescription) {
        assertNoErrorsEncountered();
        assertEquals("Unexpected description for record '" + record.getName() + "'!", expectedDescription,
                record.getDescription());
    }

    @Then("a valid record is available with fields named:")
    public void a_valid_record_is_available_with_fields_named(List<String> expectedFieldNames) {
        assertNoErrorsEncountered();
        List<RecordField> foundFields = record.getFields();
        Map<String, RecordField> foundFieldByName = Maps.uniqueIndex(foundFields, RecordField::getName);
        for (String expectedFieldName : expectedFieldNames) {
            boolean fieldExists = foundFieldByName.containsKey(expectedFieldName);
            assertTrue("Did not find expected field '" + expectedFieldName + "'!", fieldExists);
        }
    }

    @Then("the record field is available and has a description of {string}")
    public void the_record_field_is_available_and_has_a_description_of(String expectedDescription) {
        RecordField foundField = getAndValidateSingleField();
        assertEquals("Unexpected description for field '" + foundField.getName() + "'!", expectedDescription,
                foundField.getDescription());
    }

    @Then("the record field is available with a simple type of {string}")
    public void the_record_field_is_available_with_a_simple_type_of(String expectedSimpleType) {
        RecordField foundField = getAndValidateSingleField();
        assertEquals("Unexpected simple type for field '" + foundField.getName() + "'!", expectedSimpleType,
                foundField.getType().getDictionaryType().getSimpleType());
    }

    @Then("the record field is available with a column name of {string}")
    public void the_record_field_is_available_with_a_column_name_of(String expectedColumn) {
        RecordField foundField = getAndValidateSingleField();
        assertEquals("Unexpected column for field '" + foundField.getName() + "'!", expectedColumn,
                foundField.getColumn());
    }

    @Then("the record field is available and marked as required")
    public void the_record_field_is_available_and_marked_as_required() {
        RecordField foundField = getAndValidateSingleField();
        assertTrue("Expected a required value for field '" + foundField.getName() + "'!", foundField.isRequired());
    }

    @Then("the record field is available and marked as optional")
    public void the_record_field_is_available_and_marked_as_optional() {
        RecordField foundField = getAndValidateSingleField();
        assertTrue("Expected an optional value for field '" + foundField.getName() + "'!", !foundField.isRequired());
    }

    @Then("the record field is available and has a protection policy of {string}")
    public void the_record_field_is_available_and_has_a_protection_policy_of(String expectedProtectionPolicy) {
        RecordField foundField = getAndValidateSingleField();
        assertEquals("Unexpected protection policy URN for field '" + foundField.getName() + "'!",
                expectedProtectionPolicy, foundField.getProtectionPolicy());
    }

    @Then("the record field is available and has a no protection policy specified")
    public void the_record_field_is_available_and_has_a_no_protection_policy_specified() {
        RecordField foundField = getAndValidateSingleField();
        assertEquals("Expected NO protection policy URN for field '" + foundField.getName() + "'!", null,
                foundField.getProtectionPolicy());
    }

    @Then("the record field is available and has a ethics policy of {string}")
    public void the_record_field_is_available_and_has_a_ethics_policy_of(String expectedEthicsPolicy) {
        RecordField foundField = getAndValidateSingleField();
        assertEquals("Unexpected ethics policy URN for field '" + foundField.getName() + "'!", expectedEthicsPolicy,
                foundField.getEthicsPolicy());
    }

    @Then("the record field is available and has a no ethics policy specified")
    public void the_record_field_is_available_and_has_a_no_ethics_policy_specified() {
        RecordField foundField = getAndValidateSingleField();
        assertEquals("Expected NO ethics policy URN for field '" + foundField.getName() + "'!", null,
                foundField.getEthicsPolicy());
    }

    @Then("the record field is available and has a drift policy of {string}")
    public void the_record_field_is_available_and_has_a_drift_policy_of(String expectedDriftPolicy) {
        RecordField foundField = getAndValidateSingleField();
        assertEquals("Unexpected drift policy URN for field '" + foundField.getName() + "'!", expectedDriftPolicy,
                foundField.getDriftPolicy());
    }

    @Then("the record field is available and has a no drift policy specified")
    public void the_record_field_is_available_and_has_a_no_drift_policy_specified() {
        RecordField foundField = getAndValidateSingleField();
        assertEquals("Expected NO drift policy URN for field '" + foundField.getName() + "'!", null,
                foundField.getDriftPolicy());
    }

    @Then("the record is available and has data access enabled")
    public void the_record_is_available_and_has_data_access_enabled() {
        assertTrue("Expected data access to be enabled for the record!", record.getDataAccess().isEnabled());
    }

    @Then("the record is available and has data access disabled")
    public void the_record_is_available_and_has_data_access_disabled() {
        assertFalse("Expected data access to be disabled for the record!", record.getDataAccess().isEnabled());
    }

    private void assertNoErrorsEncountered() {
        assertEquals("No errors were expected!", Boolean.FALSE, encounteredError);
    }

    private RecordElement createNewRecordWithNameAndPackage(String name, String packageName) {
        RecordElement newRecord = new RecordElement();

        if (StringUtils.isNotBlank(name)) {
            newRecord.setName(name);
        }

        if (StringUtils.isNotBlank(packageName)) {
            newRecord.setPackage(packageName);
            recordPackageName = packageName;

        } else {
            recordPackageName = BOOZ_ALLEN_PACKAGE;

        }

        return newRecord;
    }

    private void assertDictionaryFoundByNameAndPackage(String expectedName, String expectedPackageName) {
        assertEquals(expectedName, record.getName());
        assertEquals(expectedPackageName, record.getPackage());
    }

    private RecordField getAndValidateSingleField() {
        assertNoErrorsEncountered();
        List<RecordField> foundFields = record.getFields();
        assertEquals("Unexpected number of  fields found!", 1, foundFields.size());
        RecordField foundField = foundFields.iterator().next();
        return foundField;
    }

    private RecordFieldElement createDefaultField(String fieldName) {
        RecordFieldElement field = new RecordFieldElement();
        field.setName(fieldName);
        RecordFieldTypeElement type = createDefaultFieldType();
        field.setType(type);
        return field;
    }

    private RecordFieldTypeElement createDefaultFieldType() {
        RecordFieldTypeElement type = new RecordFieldTypeElement();
        type.setPackage(BOOZ_ALLEN_PACKAGE);
        type.setName("ssn");
        return type;
    }

    @Given("a valid record with pyspark support")
    public void aValidRecordWithPysparkSupport() {
        final RecordElement newRecord = createNewRecordWithNameAndPackage("PySparkSupport", BOOZ_ALLEN_PACKAGE);
        final RecordFieldElement field = createDefaultField("testField");
        final FrameworkElement element = new FrameworkElement();
        element.setName(FrameworkEnum.PYSPARK);

        newRecord.addFramework(element);
        newRecord.addField(field);
        saveRecordToFile(newRecord);
    }

    @When("records are read for a Python project")
    public void recordsAreReadForAPythonProject() {
        readMetadata();
        final Map<String, Record> records = metadataRepo.getRecords(recordPackageName);
        record = records.values().iterator().next();

        final MessageTracker messageTracker = MessageTracker.getInstance();
        encounteredError = messageTracker.hasErrors();
    }

    @Then("the record is available and has Pyspark support enabled")
    public void theRecordIsAvailableAndHasPysparkSupportEnabled() {
        assertNoErrorsEncountered();
        final Optional<Framework> found = record.getFrameworks().stream()
                .filter(framework -> FrameworkEnum.PYSPARK.equals(framework.getName()))
                .findAny();

        assertTrue("Record does not contain the pyspark framework", found.isPresent());
    }

    @Given("a valid record with no pyspark support")
    public void aValidRecordWithNoPysparkSupport() {
        final RecordElement newRecord = createNewRecordWithNameAndPackage("NoPySparkSupport", BOOZ_ALLEN_PACKAGE);
        final RecordFieldElement field = createDefaultField("testField");
        newRecord.addField(field);
        saveRecordToFile(newRecord);
    }

    @Then("the record is available and has Pyspark support disabled")
    public void theRecordIsAvailableAndHasPysparkSupportDisabled() {
        assertNoErrorsEncountered();
        final Optional<Framework> found = record.getFrameworks().stream()
                .filter(framework -> FrameworkEnum.PYSPARK.equals(framework.getName()))
                .findAny();

        assertFalse("Record does not contain the pyspark framework", found.isPresent());
    }

    @Given("record B")
    public void record_b() {
        RecordElement newRecord = createNewRecordWithNameAndPackage("RecordB", RELATION_PACKAGE);
        RecordFieldElement field = new RecordFieldElement();
        field.setName("FieldB");
        RecordFieldTypeElement type = new RecordFieldTypeElement();
        type.setName("phoneNumber");
        field.setType(type);
        newRecord.addField(field);
        saveRecordToFile(newRecord);
    }

    @Given("record A has a relation to record B")
    public void record_a_references_record_b() throws IOException {
        RelationInput relationInput = new RelationInput();
        relationInput.type = "RecordB";
        relationInput.relationPackage = RELATION_PACKAGE;
        relationInput.documentation = "Relation between Record A and Record B";
        createRecordWithRelation("RecordA", TEST_RECORD_RELATIONS, relationInput);
    }

    /**
     * Uses to pass relation-level information into test steps
     */
    private static class RelationInput {
        public String documentation;
        public String type;
        public String relationPackage;
        public String multiplicity;
        public String localColumn;
    }

    @Then("the records are successfully created")
    public void the_records_are_successfully_created() {
        Record ARecord = this.metadataRepo.getRecord(TEST_RECORD_RELATIONS,"RecordA");
        Record BRecord = this.metadataRepo.getRecord(RELATION_PACKAGE,"RecordB");

        assertTrue("Parent record file was not generated", ARecord != null);
        assertTrue("Child record file was not generated", BRecord != null);
    }

    @Then("you can reference record B from record A")
    public void you_can_reference_record_b_from_record_a() {
        Record ARecord = this.metadataRepo.getRecord(TEST_RECORD_RELATIONS,"RecordA");
        assertTrue("Parent record did not have a child", ARecord.getRelations().size() > 0);
        Relation relationToRecordB = ARecord.getRelations().get(0);
        assertTrue("Child record was not of type RecordB",
                relationToRecordB.getName().equalsIgnoreCase("RecordB"));

    }
    @Then("you can reference record A from record B")
    public void you_can_reference_record_a_from_record_b() {
        Record BRecord = this.metadataRepo.getRecord(RELATION_PACKAGE,"RecordB");
        List<Record> BRecordInverseRelations = BRecord.getInverseRelations();
        assertTrue("Child record did not have a parent", BRecordInverseRelations.size() > 0);
        assertTrue("Parent record was not of type RecordA",
                BRecordInverseRelations.get(0).getTitle().equalsIgnoreCase("RecordA"));
    }

    private RecordElement createRecordWithRelation(String name, String packageName, RelationInput relation)
            throws IOException {
        RecordElement newRecord = createNewRecordWithNameAndPackage(name, packageName);

        RelationElement recordRelation = new RelationElement();
        recordRelation.setName(relation.type);
        recordRelation.setPackage(relation.relationPackage);
        recordRelation.setDocumentation(relation.documentation);
        recordRelation.setMultiplicity(relation.multiplicity);

        newRecord.addRelation(recordRelation);
        saveRecordToFile(newRecord);
        return newRecord;
    }

    @Given("a record with a relation that does not define the required fields")
    public void aRecordWithARelationThatDoesNotDefineRequired() {
        RecordElement childRecord = createNewRecordWithNameAndPackage("ChildRecord", RELATION_PACKAGE);
        RecordFieldElement field = new RecordFieldElement();
        field.setName("FieldB");
        RecordFieldTypeElement type = new RecordFieldTypeElement();
        type.setName("phoneNumber");
        field.setType(type);
        childRecord.addField(field);
        saveRecordToFile(childRecord);

        RelationElement recordRelation = new RelationElement();
        recordRelation.setName("ChildRecord");
        recordRelation.setPackage(RELATION_PACKAGE);

        RecordElement parentRecord = createNewRecordWithNameAndPackage("ParentRecord", TEST_RECORD_RELATIONS);
        parentRecord.addRelation(recordRelation);
        saveRecordToFile(parentRecord);
    }

    @Then("the relation has the correct default values")
    public void theRelationHasTheCorrectDefaultValues() {
        Record parentRecord = this.metadataRepo.getRecord(TEST_RECORD_RELATIONS, "ParentRecord");
        assertNotNull("Parent Record with relation was not created successfully", parentRecord);
        assertNotNull("Parent record does not have the appropriate relation",
                parentRecord.getRelations());
        for (Relation childRelation : parentRecord.getRelations()) {
            assertNull("Child relation should not be required", childRelation.isRequired());
            assertNull("Child relation should not have a description", childRelation.getDocumentation());
            assertNull("Child relation should not have a column", childRelation.getColumn());
            assertEquals("Child relation should default multiplicity to 1-M", Relation.Multiplicity.ONE_TO_MANY,
                    childRelation.getMultiplicity());
        }
    }
}

