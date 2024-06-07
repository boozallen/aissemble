###
# #%L
# aiSSEMBLE Data Encryption::Policy::Python
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

from aissemble_encrypt_policy import DataEncryptionPolicy
from aissemble_encrypt_policy.encrypt_input import DataEncryptionPolicyInput


class DataEncryptionPolicyManager(AbstractPolicyManager):
    """
    DataEncryptionPolicyManager class that overrides the methods in the
    policy manager to add custom configurations for the data Encryption policies.
    """

    __logger = LogManager.get_instance().get_logger("DataEncryptionPolicyManager")
    __instance = None

    def __init__(self):
        if DataEncryptionPolicyManager.__instance is not None:
            raise Exception("Class is a singleton")
        else:
            super().__init__()
            DataEncryptionPolicyManager.__instance = self

    @staticmethod
    def getInstance():
        if DataEncryptionPolicyManager.__instance is None:
            DataEncryptionPolicyManager()
        return DataEncryptionPolicyManager.__instance

    def createPolicy(self, policyIdentifier: str) -> DataEncryptionPolicy:
        return DataEncryptionPolicy(identifier=policyIdentifier)

    def setAdditionalConfigurations(self, policy: Policy, input: PolicyInput) -> None:
        if not isinstance(policy, DataEncryptionPolicy) or not isinstance(
            input, DataEncryptionPolicyInput
        ):
            raise Exception("Policy was not configured for data encryption")

        policy.encryptPhase = input.encryptPhase
        policy.encryptFields = input.encryptFields
        policy.encryptAlgorithm = input.encryptAlgorithm

    def getDeserializationClass(self) -> any:
        """
        Method that allows subclasses to override the type reference with a subclass.
        """
        return DataEncryptionPolicyInput
