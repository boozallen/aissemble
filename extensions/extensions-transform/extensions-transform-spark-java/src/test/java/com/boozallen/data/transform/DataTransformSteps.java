package com.boozallen.data.transform;

/*-
 * #%L
 * aiSSEMBLE::Extensions::Transform::Spark::Java
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.boozallen.data.transform.spark.DataTransformer;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import com.boozallen.aissemble.core.policy.configuration.policy.json.PolicyInput;
import com.boozallen.aissemble.core.policy.configuration.policy.json.rule.PolicyRuleInput;
import com.boozallen.aissemble.core.policy.configuration.util.PolicyTestUtil;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class DataTransformSteps {

    public static final String TEST_COLUMN = TestDatasetMediator.COLUMN;

    private SparkSession spark;
    private String policyIdentifier;
    private Dataset<Row> transformedDataset;

    @Before("@dataTransform")
    public void setUp() {
        spark = SparkTestHarness.getSparkSession();
        policyIdentifier = RandomStringUtils.randomAlphanumeric(10);
    }

    @After("@dataTransform")
    public void tearDown() {
        spark = null;
        policyIdentifier = null;
        transformedDataset = null;
    }

    @Given("a data transform policy has been configured")
    public void a_data_transform_policy_has_been_configured() {
        Map<String, Object> configurations = new HashMap<>();
        configurations.put("inputType", "inputTest");
        configurations.put("outputType", "outputTest");

        String className = TestDatasetMediator.class.getName();
        PolicyRuleInput rule = new PolicyRuleInput(className, configurations, null);

        PolicyInput policy = new PolicyInput(policyIdentifier);
        policy.setDescription("Test policy");
        policy.addRule(rule);

        PolicyTestUtil.writePolicyToDefaultLocation(Arrays.asList(policy), "test-policy.json");
    }

    @SuppressWarnings("unchecked")
    @When("the policy is applied on a dataset")
    public void the_policy_is_applied_on_a_dataset() {
        DataTransformer dataTransformer = new DataTransformer();
        transformedDataset = (Dataset<Row>) dataTransformer.transform(policyIdentifier, createTestDataset());
    }

    @Then("the dataset is transformed using the rule specified in the policy")
    public void the_dataset_is_transformed_using_the_rule_specified_in_the_policy() {
        StructField field = transformedDataset.schema().apply(TEST_COLUMN);
        DataType actualType = field.dataType();
        DataType expectedType = TestDatasetMediator.DATA_TYPE;

        assertTrue("Unexpected data type found after transformation", expectedType.sameType(actualType));
    }

    private Dataset<Row> createTestDataset() {
        Row row = RowFactory.create(RandomStringUtils.randomNumeric(5));
        StructType schema = new StructType().add(TEST_COLUMN, DataTypes.StringType);

        return spark.createDataFrame(Arrays.asList(row), schema);
    }

}
