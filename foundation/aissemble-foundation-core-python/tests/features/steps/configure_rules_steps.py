###
# #%L
# Policy-Based Configuration::Policy Manager (Python)
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
# #L%
###
from behave import given, when, then  # pylint: disable=no-name-in-module
from os import environ
from typing import Any, Dict, List
from policy_manager import DefaultPolicyManager
from policy_manager.configuration import PolicyConfiguration
from policy_manager.policy import (
    ConfiguredRule,
    Policy,
    Target,
)
from policy_manager.policy.json import (
    PolicyInput,
    PolicyRuleInput,
)
from krausening.logging import LogManager
from policy_test_util import PolicyTestUtil
import nose.tools as nt

environ["KRAUSENING_BASE"] = "./tests/resources/krausening/base"
configuration = PolicyConfiguration()
policyManager = DefaultPolicyManager.getInstance()

LogManager.get_instance().get_logger("AbstractPolicyManager").setLevel("DEBUG")


@given("a policy has been configured with {number:d} rules")
def step_impl(context, number):
    createValidPolicy(context, number)


@given("a policy has been configured with {number:d} targets")
def step_impl(context, number):
    createValidPolicy(context, 1)
    context.policyInput.targets = PolicyTestUtil.getRandomTargets(number)


@given("a policy has been configured with the deprecated target attribute")
def step_impl(context):
    createValidPolicy(context, 1)
    context.policyInput.target = PolicyTestUtil.getRandomTargets(1)[0]


@given("a rule within a policy has been configured without a class name")
def step_impl(context):
    context.identifier = PolicyTestUtil.getRandomString(10)
    context.policyInput = PolicyInput(identifier=context.identifier)
    context.policyInput.rules = PolicyTestUtil.getRandomRules(
        PolicyTestUtil.getRandomNumber(1, 4)
    )
    addInvalidRules(context.policyInput)


@given("a policy has no valid rules")
def step_impl(context):
    context.identifier = PolicyTestUtil.getRandomString(10)
    context.policyInput = PolicyInput(identifier=context.identifier)
    addInvalidRules(context.policyInput)


@given("a valid policy exists")
def step_impl(context):
    createValidPolicy(context, PolicyTestUtil.getRandomNumber(1, 4))


@given("the policy specifies a target")
def step_impl(context):
    row = context.table[0]
    expectedTarget = Target(retrieve_url=row["retrieveUrl"], type=row["type"])
    context.policyInput.targets = [expectedTarget]


@given("a policy exists with the following targets")
def step_impl(context):
    row1 = context.table[0]
    row2 = context.table[1]
    expectedTargets = []
    expectedTargets.append(Target(retrieve_url=row1["retrieveUrl"], type=row1["type"]))
    expectedTargets.append(Target(retrieve_url=row2["retrieveUrl"], type=row2["type"]))
    context.identifier = PolicyTestUtil.getRandomString(10)
    context.policyInput = PolicyInput(identifier=context.identifier)
    context.policyInput.targets = expectedTargets


@given("a policy rule specifies the target configurations")
def step_impl(context):
    context.expectedConfigurations = dict()
    for row in context.table:
        context.expectedConfigurations[row["key"]] = row["value"]

    context.ruleInput = PolicyRuleInput(className="TestClass")
    context.ruleInput.targetConfigurations = context.expectedConfigurations
    context.policyInput.rules.append(context.ruleInput)


@given(
    'a policy rule that uses the class "{className}" with the following configurations'
)
def step_impl(context, className):
    configurations = dict()
    for row in context.table:
        configurations[row["key"]] = row["value"]

    context.ruleInput = PolicyRuleInput(className=className)
    context.ruleInput.configurations = configurations
    context.expectedConfigurations = configurations


@when("the policy is read in")
def step_impl(context):
    policies = policyManager.policies
    policies.clear()
    policyManager.validateAndAddPolicy(context.policyInput)


@when("the policy rule is read in")
def step_impl(context):
    context.policyRule = policyManager.validateAndConfigureRule(
        context.ruleInput, Target()
    )


@then("the policy has {number:d} corresponding rules")
def step_impl(context, number):
    actualRules = getActualRules(context)
    nt.ok_(
        number == len(actualRules), "Number of configured rules did not match expected"
    )


@then("the policy has {number:d} corresponding targets")
def step_impl(context, number):
    actualTargets = getActualTargets(context)
    nt.ok_(
        number == len(actualTargets),
        "Number of configured targets did not match expected",
    )


@then("the rule is ignored")
def step_impl(context):
    actualRules = getActualRules(context)
    for actualRule in actualRules:
        nt.ok_(
            actualRule.className.strip(),
            "A rule with a blank classname was unexpectedly added",
        )


@then("the policy is ignored")
def step_impl(context):
    policies = policyManager.policies
    nt.ok_(
        context.identifier not in policies,
        "A policy with no valid rules was unexpectedly added",
    )


@then('the target type is set as "{expectedType}"')
def step_impl(context, expectedType):
    actualTarget = getActualTargets(context)[0]
    nt.ok_(
        expectedType == actualTarget.type, "The target type did not match the expected"
    )


@then('the target\'s retrieve url is set as "{expectedRetrieveUrl}"')
def step_impl(context, expectedRetrieveUrl):
    actualTarget = getActualTargets(context)[0]
    nt.ok_(
        expectedRetrieveUrl == actualTarget.retrieve_url,
        "The target retrieve url did not match the expected",
    )


@then("the configured targets are available to the rule")
def step_impl(context):
    actualRules = getActualRules(context)
    nt.ok_(len(actualRules) == 1, "The number of rules was unexpected not 1")

    actualRule = actualRules[0]
    actualConfiguredTargets = actualRule.configuredTargets
    nt.ok_(
        actualConfiguredTargets is not None,
        "Target configurations for algorithm were unexpectedly null",
    )

    for configuredTarget in actualConfiguredTargets:
        verifyConfigurations(
            context.expectedConfigurations, configuredTarget.target_configurations
        )


@then("the configurations are available to the rule")
def step_impl(context):
    actualConfigurations = context.policyRule.configurations
    nt.ok_(
        actualConfigurations is not None,
        "Configurations for algorithm were unexpectedly null",
    )
    verifyConfigurations(context.expectedConfigurations, actualConfigurations)


def createValidPolicy(context, numberRules: int) -> None:
    context.identifier = PolicyTestUtil.getRandomString(10)
    context.policyInput = PolicyInput(identifier=context.identifier)
    context.policyInput.rules = PolicyTestUtil.getRandomRules(numberRules)


def addInvalidRules(policyInput: PolicyInput) -> None:
    random = PolicyTestUtil.getRandomNumber(1, 4)
    for index in range(random):
        # Add an invalid rule
        targetConfigurations = dict()
        targetConfigurations["column"] = "transactions"
        rule = PolicyRuleInput(className="", targetConfigurations=targetConfigurations)
        policyInput.rules.append(rule)


def getActualRules(context) -> List[ConfiguredRule]:
    actualPolicy = getActualPolicy(context)
    return actualPolicy.rules


def getActualPolicy(context) -> Policy:
    policies = policyManager.policies
    nt.ok_(len(policies) == 1, "Number of policies did not match expected")

    return policies[context.identifier]


def getActualTargets(context) -> List[Target]:
    actualPolicy = getActualPolicy(context)
    actualTargets = actualPolicy.targets
    nt.ok_(actualTargets is not None, "Target was unexpectedly null")

    return actualTargets


def verifyConfigurations(
    expectedConfigurations: Dict[str, Any], actualConfigurations: Dict[str, Any]
) -> None:
    nt.ok_(
        len(expectedConfigurations) == len(actualConfigurations),
        "The number of configurations found did not match the expected number",
    )

    expectedKeys = expectedConfigurations.keys()
    for expectedKey in expectedKeys:
        nt.ok_(
            expectedKey in actualConfigurations,
            "Configurations did not contain expected key",
        )

        expectedValue = expectedConfigurations[expectedKey]
        actualValue = actualConfigurations[expectedKey]
        nt.ok_(
            expectedValue == actualValue,
            "The expected value for the key "
            + expectedKey
            + " did not match the actual value",
        )
