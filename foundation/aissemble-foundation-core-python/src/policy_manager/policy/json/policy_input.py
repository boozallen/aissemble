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
from typing import Any, Dict, List, Optional
from policy_manager.policy import AlertOptions, Target


class PolicyRuleInput(BaseModel):
    """
    PolicyRuleInput class represents policy rule data that will be read
    in from a JSON file.
    """

    """
    The className that should be used with this rule must be specified for
    the policy rule to be used.
    """
    className: str

    """
    The configuration used for the rule.
    """
    configurations: Optional[Dict[str, Any]]

    """
    Any configurations for the target set of data that is needed by this
    rule.
    """
    targetConfigurations: Optional[Dict[str, Any]]


class PolicyInput(BaseModel):
    """
    PolicyInput class represents policy information that will be read in
    from a JSON file. Used for reading and writing JSON files, but not during
    normal policy invocation.
    """

    """
    The identifier used by the service to look up the policy.
    """
    identifier: str

    """
    The description of the policy.
    """
    description: Optional[str]

    """
    The target this policy will be invoked on.
    """
    target: Optional[Target]

    """
    The optional configuration for whether alerts should be sent or not.
    """
    shouldSendAlert: Optional[AlertOptions]

    """
    The rules for this policy.
    """
    rules: List[PolicyRuleInput] = []
