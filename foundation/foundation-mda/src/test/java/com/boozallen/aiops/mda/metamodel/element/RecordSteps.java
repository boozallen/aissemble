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
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.boozallen.aiops.mda.generator.common.FrameworkEnum;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.technologybrewery.fermenter.mda.util.MessageTracker;

import com.boozallen.aiops.mda.metamodel.json.AiopsMdaJsonUtils;
import com.google.common.collect.Maps;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class RecordSteps extends AbstractModelInstanceSteps {

    protected String recordPackageName;
    protected Record record;
    protected boolean encounteredError;

    @Before("@record")
    public void setUpObjectMapper() throws Exception {
        AiopsMdaJsonUtils.configureCustomObjectMappper();

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

    @Given("a composite named {string} with multiple fields")
    public void a_composite_named_with_multiple_fields(String compositeName) {
        CompositeElement composite = new CompositeElement();
        composite.setName(compositeName);
        composite.setPackage(BOOZ_ALLEN_PACKAGE);
        for (int i = 0; i < RandomUtils.nextInt(2, 5); i++) {
            CompositeFieldElement field = new CompositeFieldElement();
            field.setName("field" + i);
            DictionaryTypeElement type = new DictionaryTypeElement();
            type.setName("ssn");
            field.setType(type);
            composite.addField(field);
        }
        saveCompositeToFile(composite);
    }

    @Given("a record with a field that has a field with a composite type of {string}")
    public void a_record_with_a_field_that_has_a_field_with_a_composite_type_of(String compositeType) {
        RecordElement newRecord = createNewRecordWithNameAndPackage("CompositeTypedFieldTest", BOOZ_ALLEN_PACKAGE);
        RecordFieldElement field = new RecordFieldElement();
        field.setName("testPolicyOverride");
        RecordFieldTypeElement type = new RecordFieldTypeElement();
        type.setPackage(BOOZ_ALLEN_PACKAGE);
        type.setName(compositeType);
        field.setType(type);
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

    @Then("the record field is available and has a field with a composite type of {string} containing multiple fields")
    public void the_record_field_is_available_and_has_a_field_with_a_composite_type_of_containing_multiple_fields(
            String expectedCompositeType) {
        
        RecordField foundField = getAndValidateSingleField();
        assertTrue("Expected to encounter a composite typed field!", foundField.getType().isCompositeTyped());
        Composite foundComposite = foundField.getType().getCompositeType();
        assertEquals("Unexpected composite type found!", expectedCompositeType, foundComposite.getName());
        assertTrue("Expected multiple fields on the found composite!", foundComposite.getFields().size() > 1);

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

}
