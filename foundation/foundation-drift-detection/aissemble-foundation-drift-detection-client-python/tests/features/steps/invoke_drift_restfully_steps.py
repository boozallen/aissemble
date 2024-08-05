###
# #%L
# Drift Detection::Python Rest Client
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
# #L%
###
from behave import given, when, then  # pylint: disable=no-name-in-module
from drift.detection.rest.client.drift_rest_client import DriftVariables
from drift.detection.rest.client.drift_rest_client import DriftVariable
from drift.detection.rest.client.drift_rest_client import DriftRestClient
from drift.detection.rest.client.drift_rest_client import DriftDetectionResult
import os

os.environ["KRAUSENING_BASE"] = "tests/resources/config/"


@given("a policy has been defined for detecting drift")
def step_impl(context):
    """
    Happens on the server side, so we'll assume that it's done. The policy name needs to match the policy in the docker module.
    """


@when(
    "I invoke drift specifying the policy using the rest service with a single drift variable"
)
def step_impl(context):
    drift_data = DriftVariable(name="testValue", value=51.2)
    drift_rest_client = DriftRestClient()

    context.response = drift_rest_client.invoke_drift(
        policy_identifier="ExamplePolicy", input=drift_data, control=None
    )


@when(
    "I invoke drift specifying the policy using the rest service with multiple drift variables"
)
def step_impl(context):
    drift_data_1 = DriftVariable(name="testValue1", value=51.2)
    drift_data_2 = DriftVariable(name="testValue2", value=47.6)
    drift_data_3 = DriftVariable(name="testValue3", value=53.9)
    list = [drift_data_1, drift_data_2, drift_data_3]
    drift_variables = DriftVariables(name="listOfVars", variables=list)
    drift_rest_client = DriftRestClient()

    context.response = drift_rest_client.invoke_drift(
        policy_identifier="ExamplePolicy", input=drift_variables, control=None
    )


@then("I receive the results of drift detection")
def step_impl(context):
    assert context.response.status_code == 200
    json = context.response.json()
    print("JSON response from drift invocation service: ")
    print(json)
    result = DriftDetectionResult(**json)
    assert False == result.hasDrift
