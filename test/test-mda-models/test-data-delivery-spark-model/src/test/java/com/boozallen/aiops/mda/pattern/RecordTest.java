package com.boozallen.aiops.mda.pattern;

/*-
 * #%L
 * AIOps Foundation::AIOps MDA Patterns::Spark
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import static org.junit.Assert.assertEquals;

import io.cucumber.java.ParameterType;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import com.boozallen.aiops.mda.pattern.record.RecordWithValidation;
import com.boozallen.aiops.mda.pattern.dictionary.StringWithValidation;
import com.boozallen.aissemble.core.exception.ValidationException;

/**
 * Implementation steps for recordWithValidation.feature
 */
public class RecordTest {
    private RecordWithValidation record;
    private StringWithValidation validationString;
    private String requiredString;
    private Boolean exceptionThrown;

    /**
     * This method is used for converting string boolean values from Cucumber into Java Boolean values
     */
    @ParameterType("true|false")
    public Boolean booleanValue(String value) {
        return Boolean.valueOf(value);
    }

    /**
     * Method for creating a new record with validation before each test run
     */
    @Before("@validatedField")
    public void setUpValidation() {
        record = new RecordWithValidation();

        //fills the required fields for the record object
        record.setRequiredSimpleType("testString");
        record.setRequiredComplexType(new StringWithValidation("testString"));

        exceptionThrown = false;
    }

    /**
     * Method for creating a string with validation based on the input
     */
    @Given("a string for validated field: {string}")
    public void a_string_for_a_validated_field(String input) {
        //cucumber does not allow null to be passed as a parameter, so must set it here
        if (input.equals("null")) {
            validationString = new StringWithValidation(null);
        }
        else {
            validationString = new StringWithValidation(input);
        }
    }

    /**
     * Method for setting the string to the appropiate field on the record
     */
    @When("I update a field that requires a minimum length of 5")
    public void i_update_a_field_that_requires_a_minimum_length_of() {
        record.setStringValidation(validationString);
    }

    /**
     * Method for creating a new record with required fields before each test run
     */
    @Before("@requiredField")
    public void setUpRequired() {
        record = new RecordWithValidation();

        //fills the other required field for the RecordWithValidation object
        record.setRequiredComplexType(new StringWithValidation("testString"));

        exceptionThrown = false;
    }

    /**
     * Method for creating a string based on the input
     */
    @Given("a string for a required field: {string}")
    public void a_string_for_a_required_field(String input) {
        //cucumber does not allow null to be passed as a parameter, so must set it here
        if (input.equals("null")) {
            requiredString = null;
        }
        else {
            requiredString = input;
        }
    }

    /**
     * Method for setting the string to the appropiate field on the record
     */
    @When("I update a required field")
    public void i_update_a_required_field() {
        record.setRequiredSimpleType(requiredString);
    }

    /**
     * Method for validating and checking for the appropiate exception
     */
    @Then("I should fail validation: {booleanValue}")
    public void i_should_fail_validation(Boolean result) {
        try {
            record.validate();
        } catch (ValidationException e) {
            exceptionThrown = true;
        }
        assertEquals(exceptionThrown, result);
    }
}
