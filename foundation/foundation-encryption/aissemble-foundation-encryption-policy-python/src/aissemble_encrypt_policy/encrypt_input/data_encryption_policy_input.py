###
# #%L
# aiSSEMBLE Data Encryption::Policy::Python
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
# #L%
###
from policy_manager.policy.json import PolicyInput
from typing import Any, Dict, List, Optional


class DataEncryptionPolicyInput(PolicyInput):
    """
    DataEncryptionPolicyInput class represents the additional policy
    configurations that are needed for data encryption.
    """

    encryptPhase: Optional[str]
    encryptFields: List[str] = []
    encryptAlgorithm: Optional[str]
