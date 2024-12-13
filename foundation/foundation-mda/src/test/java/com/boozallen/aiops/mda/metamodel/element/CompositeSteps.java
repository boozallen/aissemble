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
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.technologybrewery.fermenter.mda.generator.GenerationContext;
import org.technologybrewery.fermenter.mda.util.MessageTracker;

import com.boozallen.aiops.mda.metamodel.AissembleModelInstanceRepository;
import com.boozallen.aiops.mda.metamodel.json.AissembleMdaJsonUtils;
import com.google.common.collect.Maps;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class CompositeSteps extends AbstractModelInstanceSteps {

    protected Composite composite;
    protected String compositePackageName;
    protected boolean encounteredError;

    @Before("@composite")
    public void setUpObjectMapper() throws Exception {
        AissembleMdaJsonUtils.configureCustomObjectMappper();

        MessageTracker messageTracker = MessageTracker.getInstance();
        messageTracker.clear();

        FileUtils.deleteDirectory(GENERATED_METADATA_DIRECTORY);

    }

    @Given("a composite described by {string}, {string}")
    public void a_composite_described_by(String name, String packageName) {
        createNewCompositeWithNameAndPackage(name, packageName);
    }

    @Given("a composite with a description {string}")
    public void a_composite_with_a_description(String description) {
        CompositeElement composite = createNewCompositeWithNameAndPackage("DescriptionTest", BOOZ_ALLEN_PACKAGE);
        composite.setDescription(description);
        saveCompositeToFile(composite);
    }

    @Given("a composite with the fields named:")
    public void a_composite_with_the_fields_named(List<String> fieldNames) {
        CompositeElement composite = createNewCompositeWithNameAndPackage("FieldTest", BOOZ_ALLEN_PACKAGE);
        for (String fieldName : fieldNames) {
            CompositeFieldElement field = new CompositeFieldElement();
            field.setName(fieldName);
            DictionaryTypeElement type = new DictionaryTypeElement();
            type.setName("airportCode");
            field.setType(type);
            composite.addField(field);
        }
        saveCompositeToFile(composite);
    }

    @Given("a composite with a field that has a description {string}")
    public void a_composite_with_a_field_that_has_a_description(String description) {
        CompositeElement composite = createNewCompositeWithNameAndPackage("FieldDescriptionTest", BOOZ_ALLEN_PACKAGE);
        CompositeFieldElement field = createDefaultField("testFieldDescription");
        field.setDescription(description);
        composite.addField(field);
        saveCompositeToFile(composite);
    }

    @Given("a composite with a field that has dictionary type named {string}")
    public void a_composite_with_a_field_that_has_dictionary_type_named(String dictionaryTypeName) {
        CompositeElement composite = createNewCompositeWithNameAndPackage("CompositeDictionaryTypeTest",
                BOOZ_ALLEN_PACKAGE);
        CompositeFieldElement field = new CompositeFieldElement();
        field.setName("testType" + StringUtils.capitalize(dictionaryTypeName));
        DictionaryTypeElement type = new DictionaryTypeElement();
        type.setName(dictionaryTypeName);
        field.setType(type);
        composite.addField(field);
        saveCompositeToFile(composite);
    }

    @Given("a composite with a field specifying the column name {string}")
    public void a_composite_with_a_field_specifying_the_column_name(String columnName) {
        CompositeElement composite = createNewCompositeWithNameAndPackage("ColumnTest", BOOZ_ALLEN_PACKAGE);
        CompositeFieldElement field = createDefaultField("testColumn" + StringUtils.capitalize(columnName));
        field.setColumn(columnName);
        composite.addField(field);
        saveCompositeToFile(composite);
    }

    @Given("a composite with a field specifying the column is required")
    public void a_composite_with_a_field_specifying_the_column_is_required() {
        CompositeElement composite = createNewCompositeWithNameAndPackage("OptionalFieldTest", BOOZ_ALLEN_PACKAGE);
        CompositeFieldElement field = createDefaultField("testOptionalField");
        field.setRequired(Boolean.TRUE);
        composite.addField(field);
        saveCompositeToFile(composite);
    }

    @Given("a composite with a field specifying the column as optional")
    public void a_composite_with_a_field_specifying_the_column_as_optional() {
        CompositeElement composite = createNewCompositeWithNameAndPackage("RequiredFieldTest", BOOZ_ALLEN_PACKAGE);
        CompositeFieldElement field = createDefaultField("testRequiredField");
        field.setRequired(Boolean.FALSE);
        composite.addField(field);
        saveCompositeToFile(composite);
    }

    @Given("a composite with a field that has dictionary type named {string} and a protection policy of {string}")
    public void a_composite_with_a_field_that_has_dictionary_type_named_and_a_protection_policy_of(
            String dictionaryTypeName, String overrideProtectionPolicy) {

        CompositeElement composite = createNewCompositeWithNameAndPackage("ProtectionPolicyOverrideTest",
                BOOZ_ALLEN_PACKAGE);
        CompositeFieldElement field = new CompositeFieldElement();
        field.setName("testOverride");
        DictionaryTypeElement type = new DictionaryTypeElement();
        type.setName(dictionaryTypeName);
        field.setType(type);
        field.setProtectionPolicy(overrideProtectionPolicy);
        composite.addField(field);
        saveCompositeToFile(composite);
    }

    @Given("a composite with a field that has dictionary type named {string} and a ethics policy of {string}")
    public void a_composite_with_a_field_that_has_dictionary_type_named_and_a_ethics_policy_of(
            String dictionaryTypeName, String overrideEthicsPolicy) {

        CompositeElement composite = createNewCompositeWithNameAndPackage("EthicsPolicyOverrideTest",
                BOOZ_ALLEN_PACKAGE);
        CompositeFieldElement field = new CompositeFieldElement();
        field.setName("testOverride");
        DictionaryTypeElement type = new DictionaryTypeElement();
        type.setName(dictionaryTypeName);
        field.setType(type);
        field.setEthicsPolicy(overrideEthicsPolicy);
        composite.addField(field);
        saveCompositeToFile(composite);
    }

    @Given("a composite with a field that has dictionary type named {string} and a drift policy of {string}")
    public void a_composite_with_a_field_that_has_dictionary_type_named_and_a_drift_policy_of(String dictionaryTypeName,
            String overrideDriftPolicy) {

        CompositeElement composite = createNewCompositeWithNameAndPackage("DriftPolicyOverrideTest",
                BOOZ_ALLEN_PACKAGE);
        CompositeFieldElement field = new CompositeFieldElement();
        field.setName("testOverride");
        DictionaryTypeElement type = new DictionaryTypeElement();
        type.setName(dictionaryTypeName);
        field.setType(type);
        field.setDriftPolicy(overrideDriftPolicy);
        composite.addField(field);
        saveCompositeToFile(composite);
    }

    @When("composites are read")
    public void composites_are_read() {
        readMetadata();

        Map<String, Composite> composites = metadataRepo.getComposites(compositePackageName);
        composite = composites.values().iterator().next();

        MessageTracker messageTracker = MessageTracker.getInstance();
        encounteredError = messageTracker.hasErrors();

    }

    @Then("a valid composite is available can be looked up by the name {string} and {string}")
    public void a_valid_composite_is_available_can_be_looked_up_by_the_name_and(String expectedName,
            String expectedPackageName) {

        assertNoErrorsEncountered();
        assertDictionaryFoundByNameAndPackage(expectedName, expectedPackageName);
    }

    @Then("a valid composite is available with a description of {string}")
    public void a_valid_composite_is_available_with_a_description_of(String expectedDescription) {
        assertNoErrorsEncountered();
        assertEquals("Unexpected description for composite '" + composite.getName() + "'!", expectedDescription,
                composite.getDescription());
    }

    @Then("a valid composite is available with fields named:")
    public void a_valid_composite_is_available_with_fields_named(List<String> expectedFieldNames) {
        assertNoErrorsEncountered();
        List<CompositeField> foundFields = composite.getFields();
        Map<String, CompositeField> foundFieldByName = Maps.uniqueIndex(foundFields, CompositeField::getName);
        for (String expectedFieldName : expectedFieldNames) {
            boolean fieldExists = foundFieldByName.containsKey(expectedFieldName);
            assertTrue("Did not find expected field '" + expectedFieldName + "'!", fieldExists);
        }
    }

    @Then("the composite field is available and has a description of {string}")
    public void the_composite_field_is_available_and_has_a_description_of(String expectedDescription) {
        CompositeField foundField = getAndValidateSingleField();
        assertEquals("Unexpected description for field '" + foundField.getName() + "'!", expectedDescription,
                foundField.getDescription());
    }

    @Then("the composite field is available with a simple type of {string}")
    public void the_composite_field_is_available_with_a_simple_type_of(String expectedSimpleType) {
        CompositeField foundField = getAndValidateSingleField();
        assertEquals("Unexpected simple type for composite field '" + foundField.getName() + "'!", expectedSimpleType,
                foundField.getType().getSimpleType());
    }

    @Then("the composite field is available with a column name of {string}")
    public void the_composite_field_is_available_with_a_column_name_of(String expectedColumnName) {
        CompositeField foundField = getAndValidateSingleField();
        assertEquals("Unexpected column for field '" + foundField.getName() + "'!", expectedColumnName,
                foundField.getColumn());
    }

    @Then("the composite field is available and marked as required")
    public void the_composite_field_is_available_and_marked_as_required() {
        CompositeField foundField = getAndValidateSingleField();
        assertTrue("Expected required field '" + foundField.getName() + "'!", foundField.isRequired());
    }

    @Then("the composite field is available and marked as optional")
    public void the_composite_field_is_available_and_marked_as_optional() {
        CompositeField foundField = getAndValidateSingleField();
        assertTrue("Expected optional field '" + foundField.getName() + "'!", !foundField.isRequired());
    }

    @Then("the composite field is available and has a protection policy of {string}")
    public void the_composite_field_is_available_and_has_a_protection_policy_of(String expectedProtectionPolicy) {
        CompositeField foundField = getAndValidateSingleField();
        assertEquals("Unexpected protection policy for field '" + foundField.getName() + "'!", expectedProtectionPolicy,
                foundField.getProtectionPolicy());
    }

    @Then("the composite field is available and has a no protection policy specified")
    public void the_composite_field_is_available_and_has_a_no_protection_policy_specified() {
        CompositeField foundField = getAndValidateSingleField();
        assertEquals("No protection policy expected for field '" + foundField.getName() + "'!", null,
                foundField.getProtectionPolicy());
    }

    @Then("the composite field is available and has a ethics policy of {string}")
    public void the_composite_field_is_available_and_has_a_ethics_policy_of(String expectedEthicsPolicy) {
        CompositeField foundField = getAndValidateSingleField();
        assertEquals("Unexpected ethics policy for field '" + foundField.getName() + "'!", expectedEthicsPolicy,
                foundField.getEthicsPolicy());
    }

    @Then("the composite field is available and has a no ethics policy specified")
    public void the_composite_field_is_available_and_has_a_no_ethics_policy_specified() {
        CompositeField foundField = getAndValidateSingleField();
        assertEquals("No ethics policy expected for field '" + foundField.getName() + "'!", null,
                foundField.getEthicsPolicy());
    }

    @Then("the composite field is available and has a drift policy of {string}")
    public void the_composite_field_is_available_and_has_a_drift_policy_of(String expectedDriftPolicy) {
        CompositeField foundField = getAndValidateSingleField();
        assertEquals("Unexpected drift policy for field '" + foundField.getName() + "'!", expectedDriftPolicy,
                foundField.getDriftPolicy());
    }
    
    @Then("the composite field is available and has a no drift policy specified")
    public void the_composite_field_is_available_and_has_a_no_drift_policy_specified() {
        CompositeField foundField = getAndValidateSingleField();
        assertEquals("No drift policy expected for field '" + foundField.getName() + "'!", null,
                foundField.getDriftPolicy());
    }    

    private CompositeElement createNewCompositeWithNameAndPackage(String name, String packageName) {
        CompositeElement newComposite = new CompositeElement();

        if (StringUtils.isNotBlank(name)) {
            newComposite.setName(name);
        }

        if (StringUtils.isNotBlank(packageName)) {
            newComposite.setPackage(packageName);
            compositePackageName = packageName;

        } else {
            compositePackageName = BOOZ_ALLEN_PACKAGE;

        }

        saveCompositeToFile(newComposite);

        return newComposite;
    }

    private void assertNoErrorsEncountered() {
        assertEquals("No errors were expected!", Boolean.FALSE, encounteredError);
    }

    private void assertDictionaryFoundByNameAndPackage(String expectedName, String expectedPackageName) {
        assertEquals(expectedName, composite.getName());
        assertEquals(expectedPackageName, composite.getPackage());
    }

    private CompositeField getAndValidateSingleField() {
        assertNoErrorsEncountered();
        List<CompositeField> foundFields = composite.getFields();
        assertEquals("Unexpected number of composite fields found!", 1, foundFields.size());
        CompositeField foundField = foundFields.iterator().next();
        return foundField;
    }

    private CompositeFieldElement createDefaultField(String fieldName) {
        CompositeFieldElement field = new CompositeFieldElement();
        field.setName(fieldName);
        DictionaryTypeElement type = new DictionaryTypeElement();
        type.setName("airportCode");
        field.setType(type);
        return field;
    }

}
