###
# #%L
# Policy-Based Configuration::Policy Manager (Python)
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
# #L%
###
from pydantic import ConfigDict, BaseModel
from enum import Enum
from typing import Any, List, Dict, Optional
from krausening.logging import LogManager


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

    retrieve_url: Optional[str] = None
    type: Optional[str] = None


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

    __logger = LogManager.get_instance().get_logger("ConfiguredRule")
    __deprecated_set_methods = {"target": "set_deprecated_targetConfigurations"}
    className: str
    configurations: Optional[Dict[str, Any]] = None
    configuredTargets: Optional[List[ConfiguredTarget]] = []

    @property
    def targetConfigurations(self) -> ConfiguredTarget:
        """
        This attribute is deprecated and should not be used. ConfiguredTarget are now represented as
        a List of ConfiguredTarget objects instead of a single ConfiguredTarget attribute `targetConfigurations`.
        This attribute is replaced by `configuredTargets`.
        """
        ConfiguredRule.__logger.warn(
            "Detected use of deprecated attribute 'targetConfigurations'. Existing "
            + "usage should be moved to the new attribute 'configuredTargets'."
        )
        return self.configuredTargets[0] if len(self.configuredTargets) > 0 else None

    def set_deprecated_targetConfigurations(self, new_value: ConfiguredTarget):
        """
        This attribute is deprecated and should not be used. ConfiguredTarget are now represented as
        a List of ConfiguredTarget objects instead of a single ConfiguredTarget attribute `targetConfigurations`.
        This attribute is replaced by `configuredTargets`.
        """
        ConfiguredRule.__logger.warn(
            "Detected use of deprecated attribute 'targetConfigurations'. Existing "
            + "usage should be moved to the new attribute 'configuredTargets'."
        )
        self.configuredTargets = [new_value]

    # Links ConfiguredRule 'targetConfigurations' attribute to 'set_deprecated_targetConfigurations()' method to support
    # people still assigning values to the old attribute.
    def __setattr__(self, key, val):
        method = self.__deprecated_set_methods.get(key)
        if method is None:
            super().__setattr__(key, val)
        else:
            getattr(self, method)(val)


class Policy(BaseModel):
    """
    Policy class maps a rule or set of rules. The identifier is passed in
    during service invocation. The service uses it to find the matching policy
    and the classes and any configurations that should be used for policy
    execution.
    """

    __logger = LogManager.get_instance().get_logger("Policy")
    __deprecated_set_methods = {"target": "set_deprecated_target"}
    alertOptions: AlertOptions = AlertOptions.ON_DETECTION
    identifier: str
    description: Optional[str] = None
    targets: Optional[List[Target]] = []
    rules: List[ConfiguredRule] = []

    @property
    def target(self) -> Target:
        """
        This attribute is deprecated and should not be used. Target are now represented as
        a List of Target objects instead of a single Target attribute 'target'.
        This attribute is replaced by `targets`.
        """
        Policy.__logger.warn(
            "Detected use of deprecated attribute 'target'. Existing "
            + "usage should be moved to the new attribute 'targets'."
        )
        return self.targets[0] if len(self.targets) > 0 else None

    def set_deprecated_target(self, new_value: Target):
        """
        This attribute is deprecated and should not be used. Target are now represented as
        a List of Target objects instead of a single Target attribute 'target'.
        This attribute is replaced by `targets`.
        """
        Policy.__logger.warn(
            "Detected use of deprecated attribute 'target'. Existing "
            + "usage should be moved to the new attribute 'targets'."
        )
        self.targets = [new_value]

    model_config = ConfigDict(arbitrary_types_allowed=True)

    # Links Policy 'target' attribute to 'set_deprecated_target()' method to support
    # people still assigning values to the old attribute.
    def __setattr__(self, key, val):
        method = self.__deprecated_set_methods.get(key)
        if method is None:
            super().__setattr__(key, val)
        else:
            getattr(self, method)(val)
