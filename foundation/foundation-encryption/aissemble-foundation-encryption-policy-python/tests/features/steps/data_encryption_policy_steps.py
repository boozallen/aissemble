###
# #%L
# aiSSEMBLE Data Encryption::Policy::Python
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
# #L%
###
from behave import given, when, then  # pylint: disable=no-name-in-module
import nose.tools as nt
from policy_manager.policy.json import PolicyInput, PolicyRuleInput
from policy_manager.configuration import PolicyConfiguration
from krausening.logging import LogManager
from aiops_encrypt_policy.encrypt_input import DataEncryptionPolicyInput
from typing import List
import json, os, random, string
from aiops_encrypt_policy import DataEncryptionPolicy, DataEncryptionPolicyManager

os.environ["KRAUSENING_BASE"] = "tests/resources/krausening/base"

logger = LogManager.get_instance().get_logger("EncryptPolicyTest")
LogManager.get_instance().get_logger("AbstractPolicyManager").setLevel("DEBUG")


@given("a data encryption policy has been persisted to file")
def step_impl(context):
    """
    Create an encrypt policy file
    """
    context.policy_identifier = getRandomString(10)

    encryption_policy = DataEncryptionPolicyInput(identifier=context.policy_identifier)

    # Encryption Policy Specific Properties
    encryption_policy.encryptPhase = "ingest"
    encryption_policy.encryptFields = ["ssn"]
    encryption_policy.encryptAlgorithm = "AES_ENCRYPT"

    # RULES
    configurations = dict()
    configurations["description"] = "Apply encryption policy"
    className = "EncryptRule"
    rule = PolicyRuleInput(className=className, configurations=configurations)

    encryption_policy.rules.append(rule)

    writeEncryptPolicyToFile(encryption_policy, "test-encrypt-policy.json")
    logger.info("test-encrypt-policy.json written to file")


@when("the policy is loaded")
def step_impl(context):
    context.policyManager = DataEncryptionPolicyManager.getInstance()
    context.retrieved_policy = context.policyManager.getPolicy(
        context.policy_identifier
    )

    nt.ok_(
        context.retrieved_policy,
        "Policy could not be retrieved with identifier: " + context.policy_identifier,
    )


@then("the policy contains encryption information")
def step_impl(context):
    logger.info(
        "Encryption policy retrieved with "
        + context.retrieved_policy.encryptAlgorithm
        + " specified."
    )
    nt.ok_(
        context.retrieved_policy.encryptPhase == "ingest",
        "Policy encrypt phase was incorrect",
    )
    nt.ok_(
        len(context.retrieved_policy.encryptFields) == 1,
        "Policy encrypt field count was not 1",
    )
    nt.ok_(
        context.retrieved_policy.encryptAlgorithm == "AES_ENCRYPT",
        "Policy encrypt algorithm was incorrect",
    )


def getRandomString(length: int) -> str:
    return "".join(random.choice(string.ascii_letters) for i in range(length))


def getRandomNumber(min: int, max: int) -> int:
    return random.randint(min, max)


def writeEncryptPolicyToFile(
    policy: List[DataEncryptionPolicyInput], file: str
) -> None:
    directory = PolicyConfiguration().policiesLocation()
    filePath = os.path.join(directory, file)

    # Create the parent directory if need be
    os.makedirs(os.path.dirname(filePath), exist_ok=True)

    # Write the file
    with open(filePath, "w") as jsonFile:
        content = [policy.dict(exclude_none=True)]
        json.dump(content, jsonFile)
