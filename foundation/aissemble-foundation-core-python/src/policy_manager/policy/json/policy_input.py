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
from krausening.logging import LogManager


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
    configurations: Optional[Dict[str, Any]] = None

    """
    Any configurations for the target set of data that is needed by this
    rule.
    """
    targetConfigurations: Optional[Dict[str, Any]] = None


class PolicyInput(BaseModel):
    """
    PolicyInput class represents policy information that will be read in
    from a JSON file. Used for reading and writing JSON files, but not during
    normal policy invocation.
    """

    __logger = LogManager.get_instance().get_logger("PolicyInput")

    """
    The identifier used by the service to look up the policy.
    """
    identifier: str

    """
    The description of the policy.
    """
    description: Optional[str] = None

    """
    The targets this policy will be invoked on.
    """
    targets: Optional[List[Target]] = None

    """
    The optional configuration for whether alerts should be sent or not.
    """
    shouldSendAlert: Optional[AlertOptions] = None

    """
    The rules for this policy.
    """
    rules: List[PolicyRuleInput] = []

    """
    This attribute is deprecated and should not be used. Targets are now represented as
    a List of Target objects of a single Target attribute 'target'.
    This attribute is replaced by `targets`.
    """
    target: Optional[Target] = None

    def getAnyTargets(self) -> List[Target]:
        """
        Used to check both target attributes to contain backwards compatibility with policies still using deprecated 'target'
        """
        if self.target is not None:
            self.__logger.warn(
                "Detected use of deprecated Json Property 'target'. Existing "
                + "values should be moved to the new Json Property 'targets'."
            )
            return [self.target]
        else:
            return self.targets
