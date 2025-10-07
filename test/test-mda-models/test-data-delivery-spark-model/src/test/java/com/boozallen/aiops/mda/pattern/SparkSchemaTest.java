package com.boozallen.aiops.mda.pattern;

/*-
 * #%L
 * aiSSEMBLE::Test::MDA::Data Delivery Spark
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.boozallen.aiops.mda.pattern.record.Citizen;
import io.cucumber.datatable.DataTable;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import com.boozallen.aiops.mda.pattern.dictionary.IntegerWithValidation;
import com.boozallen.aiops.mda.pattern.dictionary.StringWithValidation;
import com.boozallen.aiops.mda.pattern.record.City;
import com.boozallen.aiops.mda.pattern.record.CitySchema;
import com.boozallen.aiops.mda.pattern.record.Mayor;
import com.boozallen.aiops.mda.pattern.record.RecordWithNonRequiredValidation;
import com.boozallen.aiops.mda.pattern.record.RecordWithNonRequiredValidationSchema;
import com.boozallen.aiops.mda.pattern.record.RecordWithRequiredValidation;
import com.boozallen.aiops.mda.pattern.record.RecordWithRequiredValidationSchema;
import com.boozallen.aiops.mda.pattern.record.State;
import com.boozallen.aiops.mda.pattern.record.Street;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class SparkSchemaTest {
    String recordWithValidatedFieldRequirement;
    CitySchema citySchema;
    RecordWithRequiredValidationSchema recordWithRequiredValidationSchema;
    RecordWithNonRequiredValidationSchema recordWithNonRequiredValidationSchema;
    RecordWithNonRequiredValidation recordWithNonRequiredValidation;
    RecordWithRequiredValidation recordWithRequiredValidation;
    List<Row> recordWithRequirementValidationRows;
    Dataset<Row> recordWithValidatedFieldDataSet;
    SparkSession spark;
    Dataset<Row> cityDataSet;
    Dataset<Row> validatedDataSet;
    private final List<String> NULL_OR_EMPTY_ARRAY = List.of("null", "[]");

    @Before("@SparkSchema")
    public void setUp() {
        this.spark = SparkTestHarness.getSparkSession();
        this.recordWithRequirementValidationRows = new ArrayList<>();
    }

    @Given("the record \"City\" exists with the following relations")
    public void theRecordExistsWithTheFollowingRelations(Map<String, String> multiplicity) {
        // Handled with MDA generation
    }

    @Given("a valid \"City\" dataSet exists")
    public void aValidDataSetExists() {
        List<Row> rows = Collections.singletonList(CitySchema.asRow(createCity("valid")));
        this.cityDataSet = spark.createDataFrame(rows, this.citySchema.getStructType());
    }

    @Given("a \"City\" dataSet with an invalid relation exists")
    public void aCityDataSetWithAnInvalidRelationExists() {
        City city = createCity("invalid");
        List<Row> rows = Collections.singletonList(CitySchema.asRow(city));
        this.cityDataSet = spark.createDataFrame(rows, this.citySchema.getStructType());
    }

    @Given("a record with a {string} field with validation rules")
    public void aRecordWithAFieldWithValidationRules(String requirement) {
        this.recordWithValidatedFieldRequirement = requirement;

        if (requirement.equals("required")) {
            this.recordWithRequiredValidation = new RecordWithRequiredValidation();
        } else {
            this.recordWithNonRequiredValidation = new RecordWithNonRequiredValidation();
        }
    }

    @Given("the field is set to a {string} value")
    public void theFieldIsSetToAValue(String validity) {
        if (this.recordWithValidatedFieldRequirement.equals("required")) {
            // set valid fields to verify validation still works with multiple fields
            this.recordWithRequiredValidation.setStringValidation(new StringWithValidation("Test123"));
            this.recordWithRequiredValidation.setStringSimple("Test123");

            if (validity.equals("valid")) {
                this.recordWithRequiredValidation.setIntegerValidation(new IntegerWithValidation(150));
            } else if(validity.equals("invalid")) {
                this.recordWithRequiredValidation.setIntegerValidation(new IntegerWithValidation(50));
            } else {
                // Do nothing to keep the field null
            }
        } else {
            // set valid fields to verify validation still works with multiple fields
            this.recordWithNonRequiredValidation.setStringValidation(new StringWithValidation("Test123"));
            this.recordWithNonRequiredValidation.setStringSimple("Test123");

            if (validity.equals("valid")) {
                this.recordWithNonRequiredValidation.setIntegerValidation(new IntegerWithValidation(150));
            } else if(validity.equals("invalid")) {
                this.recordWithNonRequiredValidation.setIntegerValidation(new IntegerWithValidation(50));
            } else {
                // Do nothing to keep the field null
            }
        }
    }

    @Given("a dataSet containing the record")
    public void aDataSetContainingTheRecord() {
        if (this.recordWithValidatedFieldRequirement.equals("required")) {
            this.recordWithRequirementValidationRows.add(RecordWithRequiredValidationSchema.asRow(this.recordWithRequiredValidation));
        } else {
            this.recordWithRequirementValidationRows.add(RecordWithNonRequiredValidationSchema.asRow(this.recordWithNonRequiredValidation));
        }
    }

    @Given("the dataset contains one valid record")
    public void theDataSetContainsOneValidRecord() {
        if (this.recordWithValidatedFieldRequirement.equals("required")) {
            RecordWithRequiredValidation validRecordWithRequiredValidation = new RecordWithRequiredValidation();
            validRecordWithRequiredValidation.setIntegerValidation(new IntegerWithValidation(150));
            validRecordWithRequiredValidation.setStringValidation(new StringWithValidation("Test123"));
            validRecordWithRequiredValidation.setStringSimple("Test123");

            this.recordWithRequirementValidationRows.add(RecordWithRequiredValidationSchema.asRow(validRecordWithRequiredValidation));
        } else {
            RecordWithNonRequiredValidation validRecordWithNonRequiredValidation = new RecordWithNonRequiredValidation();

            this.recordWithRequirementValidationRows.add(RecordWithNonRequiredValidationSchema.asRow(validRecordWithNonRequiredValidation));
        }
    }

    @When("the spark schema is generated for the \"City\" record")
    public void theSparkSchemaIsGeneratedForTheCityRecord() {
        this.citySchema = new CitySchema();
    }

    @When("a \"City\" POJO is mapped to a spark dataset using the schema")
    public void aSparkDatasetExists() {
        City expectedCity = createCity("valid");
        List<Row> cityRows = Collections.singletonList(CitySchema.asRow(expectedCity));

        this.cityDataSet = this.spark.createDataFrame(cityRows, this.citySchema.getStructType());
    }

    @When("the generated spark schema validation is performed on the dataSet")
    public void theGeneratedSparkSchemaValidationIsPerformedOnTheDataSet() {
        if (this.recordWithValidatedFieldRequirement.equals("required")) {
            this.recordWithRequiredValidationSchema = new RecordWithRequiredValidationSchema();

            this.recordWithValidatedFieldDataSet = this.spark.createDataFrame(
                this.recordWithRequirementValidationRows,
                this.recordWithRequiredValidationSchema.getStructType()
            );

            this.validatedDataSet = this.recordWithRequiredValidationSchema.validateDataFrame(this.recordWithValidatedFieldDataSet);
        } else {
            this.recordWithNonRequiredValidationSchema = new RecordWithNonRequiredValidationSchema();

            this.recordWithValidatedFieldDataSet = this.spark.createDataFrame(
                this.recordWithRequirementValidationRows,
                this.recordWithNonRequiredValidationSchema.getStructType()
            );

            this.validatedDataSet = this.recordWithNonRequiredValidationSchema.validateDataFrame(this.recordWithValidatedFieldDataSet);
        }
    }

    @Then("the schema data type for {string} is {string}")
    public void theSchemaDataTypeForIs(String record, String type) {
        assertEquals("The type for record is not correct", type,
                this.citySchema.getDataType(record.toUpperCase()).toString());
    }

    @Then("the dataset has the correct values for the relational objects")
    public void aPOJOCanBeMappedToASparkRow() {
        City expectedCity = createCity("valid");
        for (Row row : this.cityDataSet.collectAsList()) {
            City actualCity = CitySchema.mapRow(row);
            assertEquals("City did not map correctly. Incorrect number of Street relations",
                    expectedCity.getStreet().size(), actualCity.getStreet().size());
            assertEquals("City did not map correctly. Incorrect Street relation",
                    expectedCity.getStreet().get(0).toJson(), actualCity.getStreet().get(0).toJson());
            assertEquals("City did not map correctly. Incorrect Mayor relation", expectedCity.getMayor().toJson(),
                    actualCity.getMayor().toJson());
            assertEquals("City did not map correctly. Incorrect State relation", expectedCity.getState().toJson(),
                    actualCity.getState().toJson());
        }
    }

    @Then("the resulting dataSet contains {int} row\\(s)")
    public void theResultingDataSetContainsRows(int numRows) {
        assertEquals("The validated dataSet contained the incorrect number of rows", numRows, this.validatedDataSet.count());
    }

    @Given("the following City dataset:")
    public void theFollowingCityDataset(DataTable inputTable) {
        List<Map<String, String>> records = inputTable.asMaps();
        List<Row> rows = new ArrayList<>();
        for (Map<String, String> record :records) {
            rows.add(CitySchema.asRow(createCity(record.get("Mayor"), record.get("State"), record.get("Streets"), record.get("Citizen"))));
        }
        this.citySchema = new CitySchema();
        this.cityDataSet = this.spark.createDataFrame(rows, this.citySchema.getStructType());
    }

    @When("the dataset is validated against the schema")
    public void theDatasetIsValidatedAgainstTheSchema() {
        validatedDataSet = this.citySchema.validateDataFrame(this.cityDataSet);
    }

    @Then("the result dataset should match:")
    public void theResultDatasetShouldMatch(DataTable result) {
        List<Row> rows = new ArrayList<>();
        List<Map<String, String>> records = result.asMaps();
        for (Map<String, String> record :records) {
            rows.add(CitySchema.asRow(createCity(record.get("Mayor"), record.get("State"), record.get("Streets"), record.get("Citizen"))));
        }

        Dataset<Row> expectedDataset = this.spark.createDataFrame(rows, this.citySchema.getStructType());
        this.validatedDataSet.except(expectedDataset);
        assertEquals("The validated dataset has expected size.", this.validatedDataSet.count(), expectedDataset.count());
        assertEquals("The validated dataset has the expected results.", expectedDataset.except(this.validatedDataSet).isEmpty(), true);
    }

    private Mayor createMayor(String type) {
        type = type.strip();
        Mayor mayor = new Mayor();
        if (NULL_OR_EMPTY_ARRAY.contains(type)) {
            mayor = null;
        } else if (type.startsWith("valid")) {
            mayor.setName("Valid Mayor");
            mayor.setIntegerValidation(new IntegerWithValidation(100));
        } else {
            mayor.setName("invalid Mayor");
            mayor.setIntegerValidation(new IntegerWithValidation(1000));
        }
        return mayor;
    }

    private Citizen createCitizen(String type) {
        type = type.strip();
        Citizen citizen = new Citizen();
        if (type.equals("null")) {
            citizen = null;
        } else if (type.startsWith("valid")) {
            citizen.setName("Valid Citizen");
            citizen.setIntegerValidation(new IntegerWithValidation(100));
        } else {
            citizen.setName("invalid Citizen");
            citizen.setIntegerValidation(new IntegerWithValidation(1000));
        }
        return citizen;
    }

    private List<Citizen> createCitizens(String... types) {
        if (types.length == 1 && NULL_OR_EMPTY_ARRAY.contains(types[0].strip())) {
            return (List<Citizen>) getNullOrEmptyList(types[0].strip());
        }

        List<Citizen> citizens = new ArrayList<>();
        for (String type: types) {
            type = type.strip();
            citizens.add(createCitizen(type));
        }
        return citizens;
    }

    private Street createStreet(String type) {
        type = type.strip();
        Street street = new Street();
        if (type.startsWith("valid")) {
            street.setName("Valid Street");
            street.setCounty("Valid County");
            street.setIntegerValidation(new IntegerWithValidation(100));
        } else {
            street.setName("Invalid Street");
            street.setCounty("Invalid County");
            street.setIntegerValidation(new IntegerWithValidation(1000));
        }
        return street;
    }

    private List<Street> createStreets(String... types) {
        if (types.length == 1 && NULL_OR_EMPTY_ARRAY.contains(types[0].strip())) {
            return (List<Street>)getNullOrEmptyList(types[0].strip());
        }

        List<Street> streets = new ArrayList<>();
        for (String type: types) {
            type = type.strip();
            streets.add(createStreet(type));
        }
        return streets;
    }

    private State createState(String type) {
        type = type.strip();
        State state = new State();
        if (NULL_OR_EMPTY_ARRAY.contains(type)) {
            state = null;
        } else if (type.startsWith("valid")) {
            state.setName("Valid State");
        } else {
            state.setName("Invalid State");
        }
        return state;
    }

    private City createCity(String type) {
        type = type.strip();
        City city = new City();
        city.setCitizen(List.of(createCitizen(type), createCitizen(type)));
        city.setMayor(createMayor(type));
        city.setState(createState(type));
        city.setStreet(List.of(createStreet(type), createStreet(type)));
        return city;
    }

    private City createCity(String mayorValidity, String stateValidity, String streetValidity, String citizenValidity) {
        City city = new City();
        city.setMayor(createMayor(mayorValidity));
        city.setState(createState(stateValidity));
        city.setStreet(createStreets(streetValidity.split(",")));
        city.setCitizen(createCitizens(citizenValidity.split(",")));
        return city;
    }

    private Object getNullOrEmptyList(String type) {
        if (type.equals("null")) {
            return null;
        }
        return new ArrayList<>();
    }
}
