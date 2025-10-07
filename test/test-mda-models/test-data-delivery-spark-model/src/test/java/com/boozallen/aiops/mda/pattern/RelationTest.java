package com.boozallen.aiops.mda.pattern;

/*-
 * #%L
 * AIOps Foundation::AIOps MDA Patterns::Spark
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import static org.junit.Assert.assertEquals;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.boozallen.aiops.mda.pattern.record.Address;
import com.boozallen.aiops.mda.pattern.record.PersonWithMToOneRelation;
import com.boozallen.aiops.mda.pattern.record.PersonWithOneToMRelation;
import com.boozallen.aiops.mda.pattern.record.PersonWithOneToOneRelation;
import com.boozallen.aiops.mda.pattern.dictionary.Zipcode;
import com.boozallen.aiops.mda.pattern.dictionary.State;


/**
 * Test record generation for relation.feature
 */
public class RelationTest {

    private final String packageName = "com.boozallen.aiops.mda.pattern.record";
    private Map<String, String> relationClassNameMap;
    private Map<String, String> relationJsonMap;
    private String className;
    private Class<?> personClass;
    private String jsonString;
    private String multiplicity;

    private PersonWithOneToOneRelation personWithOneToOneRelation;
    private PersonWithOneToMRelation personWithOneToMRelation;
    private PersonWithMToOneRelation personWithMToOneRelation;
    private Address address;

    @Before("@relation")
    public void setupRelationClassMap() {
        relationClassNameMap = new HashMap<>();
        relationClassNameMap.put("1-1", "PersonWithOneToOneRelation");
        relationClassNameMap.put("1-M", "PersonWithOneToMRelation");
        relationClassNameMap.put("M-1", "PersonWithMToOneRelation");

        relationJsonMap = new HashMap<>();
        relationJsonMap.put("1-1", "{\"address\":{\"street\":\"123 Test St\",\"city\":\"Testville\",\"zipcode\":{\"value\":\"12345\"},\"state\":{\"value\":\"Test\"}}}");
        relationJsonMap.put("1-M", "{\"address\":[{\"street\":\"123 Test St\",\"city\":\"Testville\",\"zipcode\":{\"value\":\"12345\"},\"state\":{\"value\":\"Test\"}},{\"street\":\"123 Test St\",\"city\":\"Testville\",\"zipcode\":{\"value\":\"12345\"},\"state\":{\"value\":\"Test\"}}],\"customData\":null}");
        relationJsonMap.put("M-1", "{\"customField\":\"Test Field\",\"address\":{\"street\":\"123 Test St\",\"city\":\"Testville\",\"zipcode\":{\"value\":\"12345\"},\"state\":{\"value\":\"Test\"}},\"customData\":null}");

        address = new Address();
        address.setStreet("123 Test St");
        address.setCity("Testville");
        address.setState(new State("Test"));
        address.setZipcode(new Zipcode("12345"));
    }

    @Given("a record \"Person\" that has a {string} relation to a record \"Address\"")
    public void a_record_person_that_has_relation_to_another_record(String multiplicity) {
        this.multiplicity = multiplicity;
        className = relationClassNameMap.get(multiplicity);

        switch (multiplicity) {
            case "1-1" -> {
                this.personWithOneToOneRelation = new PersonWithOneToOneRelation();
                this.personWithOneToOneRelation.setAddress(this.address);
            }
            case "1-M" -> {
                this.personWithOneToMRelation = new PersonWithOneToMRelation();
                this.personWithOneToMRelation.setAddress(Arrays.asList(this.address, this.address));
            }
            case "M-1" -> {
                this.personWithMToOneRelation = new PersonWithMToOneRelation();
                this.personWithMToOneRelation.setAddress(this.address);
                this.personWithMToOneRelation.setCustomField("Test Field");
            }
        }
    }

    @Given("a JSON string that has a {string} record relation encoded")
    public void a_json_string_that_has_a_record_relation_encoded(String multiplicity) {
        this.multiplicity = multiplicity;
        this.jsonString = this.relationJsonMap.get(multiplicity);
    }

    @When("the \"Person\" class is generated")
    public void the_person_class_is_generated() throws ClassNotFoundException {
        personClass = Class.forName(packageName + "." + className);
    }

    @When("the record is serialized")
    public void the_record_is_serialized() {
        switch (this.multiplicity) {
            case "1-1" -> this.jsonString = this.personWithOneToOneRelation.toJson();
            case "1-M" -> this.jsonString = this.personWithOneToMRelation.toJson();
            case "M-1" -> this.jsonString = this.personWithMToOneRelation.toJson();
        }
    }

    @When("the JSON string is deserialized")
    public void the_json_string_is_deserialized() {
        switch (this.multiplicity) {
            case "1-1" -> this.personWithOneToOneRelation = PersonWithOneToOneRelation.fromJson(this.jsonString);
            case "1-M" -> this.personWithOneToMRelation = PersonWithOneToMRelation.fromJson(this.jsonString);
            case "M-1" -> this.personWithMToOneRelation = PersonWithMToOneRelation.fromJson(this.jsonString);
        }
    }

    @Then("\"Person\" has a method getAddress which returns {string}")
    public void person_has_a_method_get_address_which_returns(String type) throws NoSuchMethodException {
        Method getAddress = personClass.getMethod("getAddress", null);
        Class<?> returnType = getAddress.getReturnType();
        switch (type) {
            case "Address" -> assertClass(packageName, type, returnType);
            case "List<Address>" -> {
                String[] classNames = type.split("([<>])");
                assertClass("java.util", classNames[0], returnType);
                Type contentType = getAddress.getGenericReturnType();
                if (contentType instanceof ParameterizedType elementType) {
                    Type element = elementType.getActualTypeArguments()[0];
                    assertClass(packageName, classNames[1], (Class<?>) element);
                }
            }
        }
    }

    @Then("the record relations are maintained as JSON string")
    public void the_record_relations_are_maintained_as_json_string() {
        String expectedJsonString = this.relationJsonMap.get(this.multiplicity);
        assertEquals("Serialized JSON string did not match the expected JSON string", expectedJsonString, this.jsonString);
    }

    @Then("the record relations are maintained as a Record object")
    public void the_record_relations_are_maintained_as_a_record_object() {
        switch(this.multiplicity) {
            case "1-1":
                assertAddress(this.address, this.personWithOneToOneRelation.getAddress());
                break;
            case "M-1":
                assertAddress(this.address, this.personWithMToOneRelation.getAddress());
                assertEquals("Deserialized JSON string did not have the expected Custom Field", "Test Field", this.personWithMToOneRelation.getCustomField());
                break;
            case "1-M":
                for (Address deserializedAddress: this.personWithOneToMRelation.getAddress()) {
                    assertAddress(this.address, deserializedAddress);
                }
        }

    }

    private void assertClass(String classPackageName, String simpleClassName, Class<?> clazz) {
        assertEquals(simpleClassName,  clazz.getSimpleName());
        assertEquals(classPackageName, clazz.getPackageName());
    }

    private void assertAddress(Address expectedAddress, Address actualAddress) {
        assertEquals("Deserialized JSON string did not have the expected Street", expectedAddress.getStreet(), actualAddress.getStreet());
        assertEquals("Deserialized JSON string did not have the expected City", expectedAddress.getCity(), actualAddress.getCity());
        assertEquals("Deserialized JSON string did not have the expected State", expectedAddress.getState().getValue(), actualAddress.getState().getValue());
        assertEquals("Deserialized JSON string did not have the expected Zipcode", expectedAddress.getZipcode().getValue(), actualAddress.getZipcode().getValue());
    }
}
