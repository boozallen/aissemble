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
from enum import Enum
from typing import Any, List, Dict, Optional


class AlertOptions(Enum):
    """
    AlertOptions enum is used to determine when alerts will be sent for
    this policy.
    """

    ALWAYS = "ALWAYS"
    ON_DETECTION = "ON_DETECTION"
    NEVER = "NEVER"


class Target(BaseModel):
    """
    Target contains the target information for the policy.
    """

    retrieve_url: Optional[str]
    type: Optional[str]


class ConfiguredTarget(Target):
    """
    ConfiguredTarget contains the target information with any
    configurations needed by the rule.
    """

    target_configurations: Dict[str, Any]


class ConfiguredRule(BaseModel):
    """
    ConfiguredRule represents a rule that has been read in by the policy
    manager. It stores information about the class to be invoked as well as any
    configurations for the rule or for the policy target. The object that will
    invoke the rule will be responsible for looking up the related class.
    """

    className: str
    configurations: Optional[Dict[str, Any]]
    targetConfigurations: Optional[ConfiguredTarget]


class Policy(BaseModel):
    """
    Policy class maps a rule or set of rules. The identifier is passed in
    during service invocation. The service uses it to find the matching policy
    and the classes and any configurations that should be used for policy
    execution.
    """

    alertOptions: AlertOptions = AlertOptions.ON_DETECTION
    identifier: str
    description: Optional[str]
    target: Optional[Target]
    rules: List[ConfiguredRule] = []

    # Pydantic model config to allow policy subclasses to contain additional fields of any type
    class Config:
        arbitrary_types_allowed = True
