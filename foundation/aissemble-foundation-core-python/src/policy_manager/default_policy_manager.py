###
# #%L
# Policy-Based Configuration::Policy Manager (Python)
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
# #L%
###
from .abstract_policy_manager import AbstractPolicyManager


class DefaultPolicyManager(AbstractPolicyManager):
    """
    DefaultPolicyManager represents the singleton policy manager that can
    reuse the methods laid out by the AbstractPolicyManager.
    """

    __instance = None

    def __init__(self):
        if DefaultPolicyManager.__instance is not None:
            raise Exception("Class is a singleton")
        else:
            super().__init__()
            DefaultPolicyManager.__instance = self

    @staticmethod
    def getInstance():
        if DefaultPolicyManager.__instance is None:
            DefaultPolicyManager()
        return DefaultPolicyManager.__instance
