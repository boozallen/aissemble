# #%L
# aiSSEMBLE::Extensions::Transform::Spark::Python
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
# #L%
###
from behave import *
import nose.tools as nt
from policy_manager.policy.json import PolicyInput, PolicyRuleInput
from policy_manager.configuration import PolicyConfiguration
from data_transform_spark import DataTransformer
from pyspark.sql.types import StructType, StringType, IntegerType
from pyspark.sql import Row
from test_mediators import TestDatasetMediator
from typing import List
import json, os, random, string


os.environ["KRAUSENING_BASE"] = "./tests/resources/krausening/base"


@given("a data transform policy has been configured")
def step_impl(context):
    configurations = dict()
    configurations["inputType"] = "inputTest"
    configurations["outputType"] = "outputTest"

    className = "test_mediators.TestDatasetMediator"
    rule = PolicyRuleInput(className=className, configurations=configurations)

    policy = PolicyInput(identifier=getRandomString(10))
    policy.description = "Test policy"
    policy.rules.append(rule)

    writePolicyToFile(policy, "test-policy.json")
    context.policyIdentifier = policy.identifier


@when("the policy is applied on a dataset")
def step_impl(context):
    dataTransformer = DataTransformer()
    context.transformedDataset = dataTransformer.transform(
        context.policyIdentifier, createTestDataset(context)
    )


@then("the dataset is transformed using the rule specified in the policy")
def step_impl(context):
    transformedSchema = context.transformedDataset.schema
    testColumn = transformedSchema[TestDatasetMediator.COLUMN]
    nt.ok_(
        testColumn.dataType == TestDatasetMediator.DATA_TYPE,
        "Unexpected data type found after transformation",
    )


def getRandomString(length: int) -> str:
    return "".join(random.choice(string.ascii_letters) for i in range(length))


def getRandomNumber(min: int, max: int) -> int:
    return random.randint(min, max)


def writePolicyToFile(policy: List[PolicyInput], file: str) -> None:
    directory = PolicyConfiguration().policiesLocation()
    filePath = os.path.join(directory, file)

    # Create the parent directory if need be
    os.makedirs(os.path.dirname(filePath), exist_ok=True)

    # Write the file
    with open(filePath, "w") as jsonFile:
        content = [policy.dict(exclude_none=True)]
        json.dump(content, jsonFile)


def createTestDataset(context):
    row = Row(getRandomNumber(1000, 5000))
    schema = StructType().add(TestDatasetMediator.COLUMN, StringType(), True)
    return context.test_spark_session.createDataFrame([row], schema=schema)
