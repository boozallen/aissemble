###
# #%L
# Policy-Based Configuration::Policy Manager (Python)
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
# #L%
###
from typing import Any, Dict, List
from policy_manager import AbstractPolicyManager
from policy_manager.policy import Target
from policy_manager.policy.json import (
    PolicyInput,
    PolicyRuleInput,
)
from policy_manager.configuration import PolicyConfiguration
from krausening.logging import LogManager
import random, string, json, os
from os import environ

environ["KRAUSENING_BASE"] = "./tests/resources/krausening/base"


class PolicyTestUtil:
    availableAlgorithms = [
        "a.fake.package.and.classname",
        "com.different.company.algorithm.CustomAlgorithm",
    ]
    logger = LogManager.get_instance().get_logger("PolicyTestUtil")
    configuration = PolicyConfiguration()

    @staticmethod
    def getRandomAlgorithm() -> str:
        return random.choice(PolicyTestUtil.availableAlgorithms)

    @staticmethod
    def writePolicyToDefaultLocation(
        policies: List[PolicyInput], fileName: str
    ) -> None:
        PolicyTestUtil.writePoliciesToFile(
            policies, PolicyTestUtil.configuration.policiesLocation(), fileName
        )

    @staticmethod
    def readPoliciesFromDefaultLocation(policyManager: AbstractPolicyManager) -> None:
        policies = policyManager.policies
        policies.clear()
        policyManager.loadPolicyConfigurations(
            PolicyTestUtil.configuration.policiesLocation()
        )

    @staticmethod
    def writePoliciesToFile(
        policies: List[PolicyInput], directory: str, file: str
    ) -> None:
        filePath = os.path.join(directory, file)

        # Create the parent directory if need be
        os.makedirs(os.path.dirname(filePath), exist_ok=True)

        # Write the file
        with open(filePath, "w") as jsonFile:
            content = [policy.dict(exclude_none=True) for policy in policies]
            json.dump(content, jsonFile)

    @staticmethod
    def writePolicyToFile(policy: PolicyInput, directory: str, file: str) -> None:
        PolicyTestUtil.writePoliciesToFile([policy], directory, file)

    @staticmethod
    def getRandomPolicy() -> PolicyInput:
        identifier = PolicyTestUtil.getRandomString(10)
        target = PolicyTestUtil.getRandomTarget()
        rules = PolicyTestUtil.getRandomRules(PolicyTestUtil.getRandomNumber(2, 6))
        return PolicyInput(identifier=identifier, target=target, rules=rules)

    @staticmethod
    def getRandomRules(number: int) -> List[PolicyRuleInput]:
        rules = []
        algorithm = PolicyTestUtil.getRandomAlgorithm()

        for index in range(number):
            # Get a good mix of rule configurations written out. Just choose randomly.
            random = PolicyTestUtil.getRandomNumber(1, 5)
            randomRule = PolicyRuleInput(className=algorithm)
            if random == 1:
                randomRule = PolicyRuleInput(
                    className=algorithm,
                    configurations=PolicyTestUtil.getRandomConfigs(),
                )
            if random == 2:
                randomRule = PolicyRuleInput(
                    className=algorithm,
                    targetConfigurations=PolicyTestUtil.getRandomConfigs(),
                )
            if random == 3:
                randomRule = PolicyRuleInput(
                    className=algorithm,
                    configurations=PolicyTestUtil.getRandomConfigs(),
                    targetConfigurations=PolicyTestUtil.getRandomConfigs(),
                )

            rules.append(randomRule)

        return rules

    @staticmethod
    def getRandomTarget() -> Target:
        retrieveUrl = "http://" + PolicyTestUtil.getRandomString(5) + ".com"
        type = PolicyTestUtil.getRandomString(5)
        return Target(retrieve_url=retrieveUrl, type=type)

    @staticmethod
    def getRandomConfigs() -> Dict[str, Any]:
        configs = dict()
        configs[PolicyTestUtil.getRandomString(5)] = PolicyTestUtil.getRandomString(3)
        configs[PolicyTestUtil.getRandomString(3)] = PolicyTestUtil.getRandomNumber(
            1, 100
        )

        return configs

    @staticmethod
    def getRandomString(length: int) -> str:
        return "".join(random.choice(string.ascii_letters) for i in range(length))

    @staticmethod
    def getRandomNumber(min: int, max: int) -> int:
        return random.randint(min, max)
