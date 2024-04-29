###
# #%L
# Policy-Based Configuration::Policy Manager (Python)
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
# #L%
###
from behave import *
from os import environ, remove
from os.path import join
from policy_manager import DefaultPolicyManager
from policy_manager.policy.json import (
    PolicyInput,
    PolicyRuleInput,
)
from policy_manager.configuration import PolicyConfiguration
from krausening.logging import LogManager
from policy_test_util import PolicyTestUtil
import nose.tools as nt


environ["KRAUSENING_BASE"] = "./tests/resources/krausening/base"
configuration = PolicyConfiguration()
policyManager = DefaultPolicyManager.getInstance()

LogManager.get_instance().get_logger("AbstractPolicyManager").setLevel("DEBUG")


@given("a json file with a policy with multiple rules")
def step_impl(context):
    fileName = "multiple-rules.json"
    context.filePath = configuration.policiesLocation()
    context.policyInput = PolicyTestUtil.getRandomPolicy()
    PolicyTestUtil.writePolicyToFile(context.policyInput, context.filePath, fileName)
    context.tempFiles = [join(context.filePath, fileName)]


@given("multiple json files exist, each with a configured policy")
def step_impl(context):
    context.filePath = "./target/tests/multiple-json-files"
    context.expectedPolicies = []
    context.tempFiles = []
    randomJsonFiles = PolicyTestUtil.getRandomNumber(2, 7)

    # Create some random json file
    for index in range(randomJsonFiles):
        fileName = "policy-file-" + str(index) + ".json"
        randomPolicy = PolicyTestUtil.getRandomPolicy()
        PolicyTestUtil.writePolicyToFile(randomPolicy, context.filePath, fileName)
        context.tempFiles.append(join(context.filePath, fileName))
        context.expectedPolicies.append(randomPolicy)


@given("a policy has been configured without an identifier")
def step_impl(context):
    context.policyInput = PolicyInput(identifier="")
    rule = PolicyRuleInput(className="StandardDeviation")
    context.policyInput.rules.append(rule)

    fileName = "no-identifier.json"
    context.filePath = "target/tests/no-identifier"
    PolicyTestUtil.writePolicyToFile(context.policyInput, context.filePath, fileName)
    context.tempFiles = [join(context.filePath, fileName)]


@given("a json file with multiple policies")
def step_impl(context):
    fileName = "policies.json"
    context.filePath = "./target/tests/multiple-policies-single-file"
    context.expectedPolicies = []
    randomPolicies = PolicyTestUtil.getRandomNumber(2, 8)

    # Create some random policies
    for index in range(randomPolicies):
        randomPolicy = PolicyTestUtil.getRandomPolicy()
        context.expectedPolicies.append(randomPolicy)

    PolicyTestUtil.writePoliciesToFile(
        context.expectedPolicies, context.filePath, fileName
    )
    context.tempFiles = [join(context.filePath, fileName)]


@when("the policy is loaded from the file")
def step_impl(context):
    readPoliciesFromFilePath(context)


@when("the policies are loaded from the files")
def step_impl(context):
    readPoliciesFromFilePath(context)


@when("the policies are loaded from the file")
def step_impl(context):
    readPoliciesFromFilePath(context)


@then("the policy is available for service invocation")
def step_impl(context):
    identifier = context.policyInput.identifier
    policies = policyManager.policies
    for path in context.tempFiles:
        remove(path)
    nt.ok_(identifier in policies, "Expected policy was not found")


@then(
    "all the policies from the multiple json files are available for service invocation"
)
def step_impl(context):
    verifyMultiplePolicies(context)
    for path in context.tempFiles:
        remove(path)


@then("the policy is not added")
def step_impl(context):
    policies = policyManager.policies
    for path in context.tempFiles:
        remove(path)
    nt.ok_(len(policies) == 0, "Policies without identifier was unexpectedly added")


@then("all the policies from the file are available for service invocation")
def step_impl(context):
    for path in context.tempFiles:
        remove(path)
    verifyMultiplePolicies(context)


def verifyMultiplePolicies(context) -> None:
    actualPolicies = policyManager.policies
    nt.ok_(
        len(context.expectedPolicies) == len(actualPolicies),
        "Did not have expected number of policies",
    )

    # Verify all the policies we expect are available
    for expectedPolicy in context.expectedPolicies:
        expectedIdentifier = expectedPolicy.identifier
        nt.ok_(
            expectedIdentifier in actualPolicies,
            "The loaded policies did not contain an expected policy",
        )


def readPoliciesFromFilePath(context) -> None:
    policies = policyManager.policies
    policies.clear()
    policyManager.loadPolicyConfigurations(context.filePath)
