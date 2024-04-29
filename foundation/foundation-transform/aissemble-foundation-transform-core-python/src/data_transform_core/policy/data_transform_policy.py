# #%L
# Data Transform::Python::Core
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
# #L%
###
from policy_manager.policy import Policy
from data_transform_core.mediator import MediationManager


class DataTransformPolicy(Policy):
    """
    DataTransformPolicy class represents the additional policy
    configurations that are needed for data transform.
    """

    mediationManager: MediationManager = MediationManager()
