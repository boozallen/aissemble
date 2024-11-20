package com.boozallen.aissemble.upgrade.migration.utils;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Upgrade
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aissemble.upgrade.util.YamlUtils;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class YamlUtilsSteps {

    private Path testFile;
    private YamlUtils.YamlObject yaml;

    @Given("a YAML file:")
    public void aYamlFile(String contents) throws IOException {
        testFile = Files.createTempFile(Path.of("target"), "yaml-util-test", "");
        Files.writeString(testFile, contents);
    }

    @When("the file is loaded")
    public void theFileIsLoaded() throws IOException {
        yaml = YamlUtils.loadYaml(testFile.toFile());
    }

    @Then("the string value of the property {string} is {string}")
    public void theStringValueOfThePropertyIs(String name, String value) {
        String type = yaml.get(name).getClass().getSimpleName();
        assertTrue("Property was not loaded as string: " + name + " (was " + type + ")", yaml.hasString(name));
        assertEquals("Property value does not match for: " + name, value, yaml.getString(name));
    }

    @Then("the decimal value of the property {string} is {double}")
    public void theDecimalValueOfThePropertyIs(String name, double value) {
        String type = yaml.get(name).getClass().getSimpleName();
        assertTrue("Property was not loaded as double: " + name + " (was " + type + ")", yaml.hasDouble(name));
        assertEquals("Property value does not match for: " + name, value, yaml.getDouble(name), 0.01);
    }

    @Then("the integer value of the property {string} is {int}")
    public void theIntegerValueOfThePropertyIs(String name, int value) {
        String type = yaml.get(name).getClass().getSimpleName();
        assertTrue("Property was not loaded as int: " + name + " (was " + type + ")", yaml.hasInt(name));
        assertEquals("Property value does not match for: " + name, value, yaml.getInt(name));
    }

    @Then("the boolean value of the property {string} is {string}")
    public void theBooleanValueOfThePropertyIs(String name, String value) {
        String type = yaml.get(name).getClass().getSimpleName();
        assertTrue("Property was not loaded as boolean: " + name + " (was " + type + ")", yaml.hasBoolean(name));
        assertEquals("Property value does not match for: " + name, Boolean.valueOf(value), yaml.getBoolean(name));
    }

    @Then("the property {string} is an object")
    public void thePropertyIsAnObject(String name) {
        String type = yaml.get(name).getClass().getSimpleName();
        assertTrue("Property was not loaded as object: " + name + " (was " + type + ")", yaml.hasObject(name));
    }

    @Then("the decimal value of the property {string} of the Object {string} is {double}")
    public void theDecimalValueOfThePropertyOfTheObjectIs(String prop, String obj, double value) {
        assertTrue("Property was not loaded as double: " + obj + "." + prop, yaml.hasDouble(obj, prop));
        assertEquals("Property value does not match for: " + obj + "." + prop, value, yaml.getDouble(obj, prop), 0.01);
    }

    @Then("the size of the list {string} is {int}")
    public void theSizeOfTheListIs(String name, int size) {
        String type = yaml.get(name).getClass().getSimpleName();
        assertTrue("Property was not loaded as list: " + name + " (was " + type + ")", yaml.hasList(name));
        assertEquals("Property value does not match for: " + name, size, yaml.getList(name).size());
    }

    @Then("the {string} of item {int} of the list {string} is {int}")
    public void theIntegerValueOfItemOfTheListIs(String prop, int index, String list, int value) {
        List<YamlUtils.YamlObject> objects = yaml.getListOfObjects(list);
        YamlUtils.YamlObject object = objects.get(index);
        String key = list + "[" + index + "]." + prop;
        assertEquals("Property value does not match for: " + key, value, object.getInt(prop));
    }
}
