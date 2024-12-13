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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import io.cucumber.java.After;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.technologybrewery.fermenter.mda.GenerateSourcesHelper.LoggerDelegate;
import org.technologybrewery.fermenter.mda.util.JsonUtils;
import org.technologybrewery.fermenter.mda.util.MessageTracker;

import com.boozallen.aiops.mda.metamodel.LoggerDelegateImpl;
import com.boozallen.aiops.mda.metamodel.json.AissembleMdaJsonUtils;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class DictionarySteps extends AbstractModelInstanceSteps {

    protected File dictionaryFile;
    protected Dictionary dictionary;
    protected boolean encounteredError;

    @Before("@dictionary")
    public void setUpObjectMapper() {
        AissembleMdaJsonUtils.configureCustomObjectMappper();

        MessageTracker messageTracker = MessageTracker.getInstance();
        messageTracker.clear();

    }

    @After("@dictionary")
    public void cleanUp() throws IOException {
        FileUtils.cleanDirectory(new File(dictionaryFile.getParent()));
    }

    @Given("a dictionary described by {string}, {string}")
    public void a_dictionary_described_by(String name, String packageName) throws Exception {
        DictionaryElement newDictionary = new DictionaryElement();

        if (StringUtils.isNotBlank(name)) {
            newDictionary.setName(name);
        }

        if (StringUtils.isNotBlank(packageName)) {
            newDictionary.setPackage(packageName);
        }

        dictionaryFile = saveDictionaryToFile(newDictionary);
    }

    @Given("a dictionary with the following types:")
    public void a_dictionary_with_the_following_types(List<DictionaryTypeElement> dictionaryTypes) throws Exception {
        createDictionaryWithTypes(dictionaryTypes);
    }

    @Given("a dictionary with the formats {string}")
    public void a_dictionary_with_the_formats(String formatsAsString) throws Exception {
        List<String> formats = getFormatsFromCucumberVariable(formatsAsString);

        DictionaryTypeElement formatDictionaryTestType = createDictionaryType("format", "string");

        ValidationElement validation = new ValidationElement();
        validation.setFormats(formats);
        formatDictionaryTestType.setValidation(validation);

        createDictionaryWithType(formatDictionaryTestType);

    }

    @Given("a dictionary type with empty string as a format")
    public void a_dictionary_type_with_empty_string_as_a_format() throws Exception {
        DictionaryTypeElement formatDictionaryTestType = createDictionaryType("emptyStringFormat", "string");

        ValidationElement validation = new ValidationElement();
        List<String> foramts = Lists.newArrayList("    ", " ", "");
        validation.setFormats(foramts);
        formatDictionaryTestType.setValidation(validation);

        createDictionaryWithType(formatDictionaryTestType);
    }

    @Given("a dictionary type with length validation {int} and {int}")
    public void a_dictionary_type_with_length_validation_and(Integer minLength, Integer maxLength) throws Exception {
        createDictionaryWithMinAndMaxLengths(minLength, maxLength);
    }

    @Given("a dictionary type with min length validation {int}")
    public void a_dictionary_type_with_min_length_validation(Integer minLength) throws Exception {
        createDictionaryWithMinAndMaxLengths(minLength, null);
    }

    @Given("a dictionary type with max length validation {int}")
    public void a_dictionary_type_with_max_length_validation(Integer maxLength) throws Exception {
        createDictionaryWithMinAndMaxLengths(null, maxLength);
    }

    @Given("a dictionary type with range validation {string} and {string}")
    public void a_dictionary_type_with_range_validation_and(String minValue, String maxValue) throws Exception {
        createDictionaryWithMinAndMaxRange(minValue, maxValue);
    }

    @Given("a dictionary type with scale of {int}")
    public void a_dictionary_type_with_scale_of(Integer scale) throws Exception {
        DictionaryTypeElement scaleDictionaryTestType = createDictionaryType("scale", "decimal");

        ValidationElement validation = new ValidationElement();
        validation.setScale(scale);
        scaleDictionaryTestType.setValidation(validation);

        createDictionaryWithType(scaleDictionaryTestType);
    }

    @Given("a dictionary type with protection policy URN of {string}")
    public void a_dictionary_type_with_protection_policy_urn_of(String protectionPolicyUrn) throws Exception {
        DictionaryTypeElement protectionPolicyDictionaryTestType = createDictionaryType("protectionPolicy", "string");
        protectionPolicyDictionaryTestType.setProtectionPolicy(protectionPolicyUrn);

        createDictionaryWithType(protectionPolicyDictionaryTestType);
    }

    @Given("a dictionary type with an empty protection policy URN")
    public void a_dictionary_type_with_an_empty_protection_policy_urn() throws Exception {
        DictionaryTypeElement protectionPolicyType = createDictionaryType("emptyProtectionPolicy", "string");
        protectionPolicyType.setProtectionPolicy("    ");

        createDictionaryWithType(protectionPolicyType);
    }

    @Given("a dictionary type with ethics policy URN of {string}")
    public void a_dictionary_type_with_ethics_policy_urn_of(String ethicsPolicyUrn) throws Exception {
        DictionaryTypeElement ethicsPolicyDictionaryTestType = createDictionaryType("ethicsPolicy", "string");
        ethicsPolicyDictionaryTestType.setEthicsPolicy(ethicsPolicyUrn);

        createDictionaryWithType(ethicsPolicyDictionaryTestType);
    }

    @Given("a dictionary type with an empty ethics policy URN")
    public void a_dictionary_type_with_an_empty_ethics_policy_urn() throws Exception {
        DictionaryTypeElement protectionPolicyDictionaryTestType = createDictionaryType("emptyEthicsPolicy", "string");
        protectionPolicyDictionaryTestType.setEthicsPolicy("");

        createDictionaryWithType(protectionPolicyDictionaryTestType);
    }

    @Given("a dictionary type with drift policy URN of {string}")
    public void a_dictionary_type_with_drift_policy_urn_of(String driftPolicyUrn) throws Exception {
        DictionaryTypeElement driftPolicyDictionaryTestType = createDictionaryType("driftPolicy", "string");
        driftPolicyDictionaryTestType.setDriftPolicy(driftPolicyUrn);

        createDictionaryWithType(driftPolicyDictionaryTestType);
    }

    @Given("a dictionary type with an empty drift policy URN")
    public void a_dictionary_type_with_an_empty_drift_policy_urn() throws Exception {
        DictionaryTypeElement protectionPolicyDictionaryTestType = createDictionaryType("emptyDriftPolicy", "string");
        protectionPolicyDictionaryTestType.setDriftPolicy("\t");

        createDictionaryWithType(protectionPolicyDictionaryTestType);
    }

    @When("dictionaries are read")
    public void dictionaries_are_read() {
        dictionary = JsonUtils.readAndValidateJson(dictionaryFile, DictionaryElement.class);
        assertNotNull("Could not read dictionary file!", dictionary);
        dictionary.validate();

        MessageTracker messageTracker = MessageTracker.getInstance();
        LoggerDelegate loggerDelegate = new LoggerDelegateImpl();
        messageTracker.emitMessages(loggerDelegate);

        encounteredError = messageTracker.hasErrors();

    }

    @Then("a valid dictionary is available can be looked up by the name {string} and {string}")
    public void a_valid_dictionary_is_available_can_be_looked_up_by_the_name_and(String expectedName,
            String expectedPackageName) {

        assertNoErrorsEncountered();
        assertDictionaryFoundByNameAndPackage(expectedName, expectedPackageName);
    }

    @Then("the dictionary returns the following types:")
    public void the_dictionary_returns_the_following_types(List<DictionaryTypeElement> expectedDictionaryTypes) {
        assertNoErrorsEncountered();
        List<DictionaryType> foundDictionaryTypes = dictionary.getDictionaryTypes();
        Map<String, DictionaryType> foundDictionaryTypesByName = Maps.uniqueIndex(foundDictionaryTypes,
                DictionaryType::getName);

        for (DictionaryTypeElement expectedDictionaryType : expectedDictionaryTypes) {
            String expectedName = expectedDictionaryType.getName();
            DictionaryType foundType = foundDictionaryTypesByName.get(expectedName);
            assertNotNull(expectedName = " was not found!", foundType);
            assertEquals("Unexpected 'simpleType' encountered!", expectedDictionaryType.getSimpleType(),
                    foundType.getSimpleType());
        }
    }

    @Then("a valid dictionary type is available that contains the formats {string}")
    public void a_valid_dictionary_type_is_available_that_contains_the_formats(String expectedFormatsAsString) {
        assertNoErrorsEncountered();
        List<String> expectedFormats = getFormatsFromCucumberVariable(expectedFormatsAsString);
        DictionaryType foundDictionaryType = findExpectedDictionaryType();
        Validation foundValidation = findAndAssertValidation(foundDictionaryType);

        Collection<String> foundFormats = foundValidation.getFormats();
        assertEquals("Unexpected number of formats enountered!", expectedFormats.size(), foundFormats.size());
        for (String expectedFormat : expectedFormats) {
            assertTrue("Cound not find format '" + expectedFormat + "'!", foundFormats.contains(expectedFormat));
        }
    }

    @Then("a valid dictionary type is available that does not contains any formats")
    public void a_valid_dictionary_type_is_available_that_does_not_contains_any_formats() {
        DictionaryType foundDictionaryType = assertNoErrorsAndReturnDictionaryType();
        Validation foundValidation = findAndAssertValidation(foundDictionaryType);

        Collection<String> foundFormats = foundValidation.getFormats();
        assertEquals("Unexpected number of formats enountered!", 0, foundFormats.size());
    }

    @Then("a valid dictionary type is available that contains the length validations {int} and {int}")
    public void a_valid_dictionary_type_is_available_that_contains_the_length_validations_and(Integer expectedMinLength,
            Integer expectedMaxLength) {

        DictionaryType foundDictionaryType = assertNoErrorsAndReturnDictionaryType();
        Validation foundValidation = findAndAssertValidation(foundDictionaryType);
        assertEquals("Unexpected minLength encountered!", expectedMinLength, foundValidation.getMinLength());
        assertEquals("Unexpected maxLength encountered!", expectedMaxLength, foundValidation.getMaxLength());
    }

    @Then("a valid dictionary type is available that contains the min length {int}")
    public void a_valid_dictionary_type_is_available_that_contains_the_min_length(Integer expectedMinLength) {
        DictionaryType foundDictionaryType = assertNoErrorsAndReturnDictionaryType();
        Validation foundValidation = findAndAssertValidation(foundDictionaryType);
        assertEquals("Unexpected minLength encountered!", expectedMinLength, foundValidation.getMinLength());
    }

    @Then("a valid dictionary type is available that contains the max length {int}")
    public void a_valid_dictionary_type_is_available_that_contains_the_max_length(Integer expectedMaxLength) {
        DictionaryType foundDictionaryType = assertNoErrorsAndReturnDictionaryType();
        Validation foundValidation = findAndAssertValidation(foundDictionaryType);
        assertEquals("Unexpected maxLength encountered!", expectedMaxLength, foundValidation.getMaxLength());
    }

    @Then("a valid dictionary type returns an error around length validation")
    public void a_valid_dictionary_type_returns_an_error_around_length_validation() {
        assertTrue("Expected a length-related!", encounteredError);
    }

    @Then("a valid dictionary type is available that contains the range validations {string} and {string}")
    public void a_valid_dictionary_type_is_available_that_contains_the_range_validations_and(String expectedMinValue,
            String expectedMaxValue) {

        DictionaryType foundDictionaryType = assertNoErrorsAndReturnDictionaryType();
        Validation foundValidation = findAndAssertValidation(foundDictionaryType);
        assertEquals("Unexpected minValue encountered!", expectedMinValue, foundValidation.getMinValue());
        assertEquals("Unexpected maxValue encountered!", expectedMaxValue, foundValidation.getMaxValue());

    }

    @Then("a valid dictionary type returns an error around range validation")
    public void a_valid_dictionary_type_returns_an_error_around_range_validation() {
        assertTrue("Expected a range-related!", encounteredError);
    }

    @Then("a valid dictionary type is available that contains the scale {int}")
    public void a_valid_dictionary_type_is_available_that_contains_the_scale(Integer expectedScale) {
        DictionaryType foundDictionaryType = assertNoErrorsAndReturnDictionaryType();
        Validation foundValidation = findAndAssertValidation(foundDictionaryType);
        assertEquals("Unexpected scale encountered!", expectedScale, foundValidation.getScale());
    }

    @Then("a valid dictionary type returns an error around scale validation")
    public void a_valid_dictionary_type_returns_an_error_around_scale_validation() {
        assertTrue("Expected a scale-related!", encounteredError);
    }

    @Then("a valid dictionary type is available that contains the protection policy URN {string}")
    public void a_valid_dictionary_type_is_available_that_contains_the_protection_policy_urn(
            String expectedProtectionPolicyUrn) {

        DictionaryType foundDictionaryType = assertNoErrorsAndReturnDictionaryType();
        assertEquals("Unexpected protection policy encountered!", expectedProtectionPolicyUrn,
                foundDictionaryType.getProtectionPolicy());
    }

    @Then("a valid dictionary type is available that contains no protection policy URN")
    public void a_valid_dictionary_type_is_available_that_contains_no_protection_policy_urn() {
        DictionaryType foundDictionaryType = assertNoErrorsAndReturnDictionaryType();
        assertEquals("Protection policy should NOT have been encountered!", null,
                foundDictionaryType.getProtectionPolicy());
    }

    @Then("a valid dictionary type is available that contains the ethics policy URN {string}")
    public void a_valid_dictionary_type_is_available_that_contains_the_ethics_policy_urn(
            String expectedEthicsPolicyUrn) {

        DictionaryType foundDictionaryType = assertNoErrorsAndReturnDictionaryType();
        assertEquals("Unexpected ethics policy encountered!", expectedEthicsPolicyUrn,
                foundDictionaryType.getEthicsPolicy());
    }

    @Then("a valid dictionary type is available that contains no ethics policy URN")
    public void a_valid_dictionary_type_is_available_that_contains_no_ethics_policy_urn() {
        DictionaryType foundDictionaryType = assertNoErrorsAndReturnDictionaryType();
        assertEquals("Ethics policy should NOT have been encountered!", null, foundDictionaryType.getEthicsPolicy());
    }

    @Then("a valid dictionary type is available that contains the drift policy URN {string}")
    public void a_valid_dictionary_type_is_available_that_contains_the_drift_policy_urn(String expectedDriftPolicyUrn) {
        DictionaryType foundDictionaryType = assertNoErrorsAndReturnDictionaryType();
        assertEquals("Unexpected drift policy encountered!", expectedDriftPolicyUrn,
                foundDictionaryType.getDriftPolicy());
    }

    @Then("a valid dictionary type is available that contains no drift policy URN")
    public void a_valid_dictionary_type_is_available_that_contains_no_drift_policy_urn() {
        DictionaryType foundDictionaryType = assertNoErrorsAndReturnDictionaryType();
        assertEquals("Drift policy should NOT have been encountered!", null, foundDictionaryType.getDriftPolicy());
    }
    
    private DictionaryType assertNoErrorsAndReturnDictionaryType() {
        assertNoErrorsEncountered();
        DictionaryType foundDictionaryType = findExpectedDictionaryType();
        return foundDictionaryType;
    }    

    private Validation findAndAssertValidation(DictionaryType foundDictionaryType) {
        Validation foundValidation = foundDictionaryType.getValidation();
        assertNotNull("Expected a non-null validation instance!", foundValidation);

        return foundValidation;
    }

    private void assertNoErrorsEncountered() {
        assertEquals("No errors were expected!", Boolean.FALSE, encounteredError);
    }

    private DictionaryType findExpectedDictionaryType() {
        List<DictionaryType> foundDictionaryTypes = dictionary.getDictionaryTypes();
        assertEquals("Expected exactly 1 dictionary type to be found!", 1, foundDictionaryTypes.size());
        return foundDictionaryTypes.iterator().next();
    }

    private void assertDictionaryFoundByNameAndPackage(String expectedName, String expectedPackageName) {
        assertEquals(expectedName, dictionary.getName());
        assertEquals(expectedPackageName, dictionary.getPackage());
    }

    private List<String> getFormatsFromCucumberVariable(String formatsAsString) {
        Splitter formatSplitter = Splitter.on(",");
        List<String> formats = formatSplitter.splitToList(formatsAsString);
        return formats;
    }

    private void createDictionaryWithType(DictionaryTypeElement dictionaryType) throws Exception {
        List<DictionaryTypeElement> dictionaryTypes = new ArrayList<>();
        dictionaryTypes.add(dictionaryType);
        createDictionaryWithTypes(dictionaryTypes);
    }

    private void createDictionaryWithTypes(List<DictionaryTypeElement> dictionaryTypes) throws Exception {
        DictionaryElement newDictionary = new DictionaryElement();
        newDictionary.setPackage(BOOZ_ALLEN_PACKAGE);
        String name = "test-" + RandomStringUtils.insecure().nextAlphabetic(5);
        newDictionary.setName(name);
        for (DictionaryTypeElement dictionaryType : dictionaryTypes) {
            newDictionary.addDictionaryType(dictionaryType);
        }

        dictionaryFile = saveDictionaryToFile(newDictionary);
    }

    private void createDictionaryWithMinAndMaxLengths(Integer minLength, Integer maxLength) throws Exception {
        DictionaryTypeElement lengthDictionaryTestType = new DictionaryTypeElement();
        lengthDictionaryTestType.setName("length" + RandomStringUtils.insecure().nextAlphabetic(5));
        lengthDictionaryTestType.setSimpleType("string");

        ValidationElement validation = new ValidationElement();
        validation.setMinLength(minLength);
        validation.setMaxLength(maxLength);
        lengthDictionaryTestType.setValidation(validation);

        createDictionaryWithType(lengthDictionaryTestType);
    }

    private void createDictionaryWithMinAndMaxRange(String minValue, String maxValue) throws Exception {
        DictionaryTypeElement rangeDictionaryTestType = new DictionaryTypeElement();
        rangeDictionaryTestType.setName("range" + RandomStringUtils.insecure().nextAlphabetic(5));
        if (minValue.contains(".") || maxValue.contains(".")) {
            rangeDictionaryTestType.setSimpleType("decimal");
        } else {
            rangeDictionaryTestType.setSimpleType("integer");
        }

        ValidationElement validation = new ValidationElement();
        validation.setMinValue(minValue);
        validation.setMaxValue(maxValue);
        rangeDictionaryTestType.setValidation(validation);

        createDictionaryWithType(rangeDictionaryTestType);
    }

}
