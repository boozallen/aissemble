# #%L
# Data Transform::Python::Core
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
# #L%
###
from typing import List
from krausening.logging import LogManager
from policy_manager import AbstractPolicyManager
from policy_manager.policy import Policy
from policy_manager.policy.json import PolicyInput, PolicyRuleInput
from data_transform_core.mediator import MediationConfiguration, MediationProperty
from data_transform_core.policy import DataTransformPolicy
from data_transform_core import DataTransformException


class DataTransformPolicyManager(AbstractPolicyManager):
    """
    DataTransformPolicyManager class that overrides the methods in the
    policy manager to add custom configurations for the data transform policies.
    """

    __logger = LogManager.get_instance().get_logger("DataTransformPolicyManager")
    __instance = None

    def __init__(self):
        if DataTransformPolicyManager.__instance is not None:
            raise Exception("Class is a singleton")
        else:
            super().__init__()
            DataTransformPolicyManager.__instance = self

    @staticmethod
    def getInstance():
        if DataTransformPolicyManager.__instance is None:
            DataTransformPolicyManager()
        return DataTransformPolicyManager.__instance

    def createPolicy(self, policyIdentifier: str) -> DataTransformPolicy:
        return DataTransformPolicy(identifier=policyIdentifier)

    def setAdditionalConfigurations(self, policy: Policy, input: PolicyInput) -> None:
        if not isinstance(policy, DataTransformPolicy):
            raise DataTransformException("Policy was not configured for data transform")

        mediationManager = policy.mediationManager
        mediationConfigurations = self.createMediationConfigurations(input.rules)

        for mediationConfiguration in mediationConfigurations:
            mediationManager.validateAndAddMediator(
                mediationConfigurations, mediationConfiguration
            )

    def createMediationConfigurations(
        self, ruleInputs: List[PolicyRuleInput]
    ) -> List[MediationConfiguration]:
        mediationConfigurations = []
        for ruleInput in ruleInputs:
            mediationConfiguration = self.createMediationConfiguration(ruleInput)
            mediationConfigurations.append(mediationConfiguration)

        return mediationConfigurations

    def createMediationConfiguration(
        self, ruleInput: PolicyRuleInput
    ) -> MediationConfiguration:
        mediationConfiguration = None
        try:
            className = ruleInput.className
            inputType = ruleInput.configurations.get("inputType")
            outputType = ruleInput.configurations.get("outputType")
            properties = ruleInput.configurations.get("properties")

            mediationConfiguration = MediationConfiguration(
                className=className, inputType=inputType, outputType=outputType
            )
            if properties:
                mediationConfiguration.properties = [
                    MediationProperty(
                        key=property.get("key"), value=property.get("value")
                    )
                    for property in properties
                ]

            DataTransformPolicyManager.__logger.debug(
                "Created mediation configuration: %s" % mediationConfiguration
            )

        except Exception:
            raise DataTransformException("Invalid configurations found in rule")

        return mediationConfiguration
