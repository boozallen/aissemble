###
# #%L
# Policy-Based Configuration::Policy Manager (Python)
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
# #L%
###
from pydantic import BaseModel
from typing import Optional


class PolicyInvocationResult(BaseModel):
    """
    PolicyInvocationResult represents the results of a policy invocation.
    """

    policyName: str
    policyDescription: Optional[str] = None
    timestamp: str
