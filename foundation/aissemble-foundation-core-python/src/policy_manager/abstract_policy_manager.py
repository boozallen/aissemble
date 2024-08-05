###
# #%L
# Policy-Based Configuration::Policy Manager (Python)
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
# #L%
###
from abc import ABC
from typing import Dict, List
from os import environ, listdir, path
from pathlib import Path
from json import load
from time import time_ns
from krausening.logging import LogManager
from policy_manager.configuration import PolicyConfiguration
from policy_manager.policy import (
    ConfiguredRule,
    ConfiguredTarget,
    Policy,
    Target,
)
from policy_manager.policy.json import (
    PolicyInput,
    PolicyRuleInput,
)


class AbstractPolicyManager(ABC):
    """
    AbstractPolicyManager is the abstract class that contains the logic for
    reading and parsing policies and rules from a json configuration file.
    """

    __logger = LogManager.get_instance().get_logger("AbstractPolicyManager")
    __policyLocation = "POLICY_LOCATION"

    def __init__(self) -> None:
        self._configuration = PolicyConfiguration()
        self._policies: Dict[str, Policy] = dict()

        policiesLocation = self.getPoliciesLocation()
        self.loadPolicyConfigurations(policiesLocation)

    def getPoliciesLocation(self) -> str:
        """
        Helper method that uses the policy configurations from PropertyManager
        by default, but can be overridden by a system property POLICY_LOCATION
        """
        policiesLocation = self._configuration.policiesLocation()
        systemProperty = environ.get(AbstractPolicyManager.__policyLocation, None)

        # Override the configured policy location with system property if it's set
        if systemProperty and systemProperty.strip():
            policiesLocation = systemProperty

        return policiesLocation

    def loadPolicyConfigurations(self, policiesLocation: str) -> None:
        """
        Method that loads policy configurations from json files. Checks that the
        path exists and sets up default policies if none are loaded.
        """
        start = time_ns() / 1000000
        AbstractPolicyManager.__logger.debug(
            "Loading policies from %s" % policiesLocation
        )

        if policiesLocation:
            self.loadJsonFilesInDirectory(policiesLocation)

        if not self._policies:
            AbstractPolicyManager.__logger.error("No policies were configured!")

        stop = time_ns() / 1000000
        AbstractPolicyManager.__logger.debug(
            "Loaded %s policy configurations in %s ms"
            % (len(self._policies), stop - start)
        )

    def loadJsonFilesInDirectory(self, policyConfigurationsLocation: str) -> None:
        locationPath = Path(policyConfigurationsLocation)
        if locationPath.exists():
            files = listdir(policyConfigurationsLocation)
            if not files:
                AbstractPolicyManager.__logger.warn(
                    "No files were found within: %s" % policyConfigurationsLocation
                )
            else:
                for fileName in files:
                    filePath = path.join(locationPath, fileName)
                    self.loadJsonFile(filePath, fileName)

        else:
            AbstractPolicyManager.__logger.error(
                "Policy file location does not exist. No policies will be loaded..."
            )

    def loadJsonFile(self, filePath: str, fileName: str) -> None:
        try:
            with open(filePath) as file:
                configuredPolicies = [
                    self.getDeserializationClass().parse_obj(policy)
                    for policy in load(file)
                ]
                if configuredPolicies:
                    self.validateAndAddPolicies(configuredPolicies)
                else:
                    AbstractPolicyManager.__logger.error(
                        "No policies were found in policy configuration file: %s"
                        % fileName
                    )

        except Exception as e:
            AbstractPolicyManager.__logger.error(
                "Could not read policy configuration file: %s because of %s"
                % (fileName, str(e))
            )

    def validateAndAddPolicies(self, policies: List[PolicyInput]) -> None:
        """
        Method that adds a list of policies that's been read in from JSON.
        """
        for policyInput in policies:
            self.validateAndAddPolicy(policyInput)

    def validateAndAddPolicy(self, policyInput: PolicyInput) -> None:
        """
        Method that validates a policy that's been read in from JSON
        and sets up the corresponding rules.
        """
        policyIdentifier = policyInput.identifier

        # Make sure the policy has an identifier, since we need it for policy lookup
        if policyIdentifier.strip():
            configuredPolicy = self.createPolicy(policyIdentifier)
            configuredPolicy.description = policyInput.description

            # Set the alert options
            if policyInput.shouldSendAlert:
                configuredPolicy.alertOptions = policyInput.shouldSendAlert

            # Set the targets
            targets = policyInput.getAnyTargets()
            configuredPolicy.targets = targets

            # Set any additional configurations
            self.setAdditionalConfigurations(configuredPolicy, policyInput)

            ruleInputs = policyInput.rules
            if ruleInputs:
                for ruleInput in ruleInputs:
                    # Add the policy rule if everything loads successfully
                    configuredRule = self.validateAndConfigureRule(ruleInput, targets)
                    if configuredRule:
                        configuredPolicy.rules.append(configuredRule)

            # Make sure the policy ended up with at least one configured rule,
            # otherwise don't add it
            rules = configuredPolicy.rules
            if rules:
                AbstractPolicyManager.__logger.debug(
                    "Adding policy %s with %s rules configured"
                    % (configuredPolicy, len(configuredPolicy.rules))
                )
                self._policies[policyIdentifier] = configuredPolicy
            else:
                AbstractPolicyManager.__logger.warn(
                    "Policy rules could not be loaded. Skipping policy %s..."
                    % policyIdentifier
                )

        else:
            AbstractPolicyManager.__logger.warn(
                "Policies MUST have an identifier. Skipping policy with no identifier..."
            )

    def validateAndConfigureRule(
        self, ruleInput: PolicyRuleInput, targets: List[Target]
    ) -> ConfiguredRule:
        className = ruleInput.className
        configurations = ruleInput.configurations
        targetConfigurations = ruleInput.targetConfigurations

        configuredRule = None

        if className.strip():
            configuredTargets = []
            if targets:
                for target in targets:
                    if targetConfigurations:
                        configuredTarget = ConfiguredTarget(
                            target_configurations=targetConfigurations
                        )

                        if target:
                            configuredTarget.retrieve_url = target.retrieve_url
                            configuredTarget.type = target.type

                        configuredTargets.append(configuredTarget)

            configuredRule = ConfiguredRule(
                className=className,
                configurations=configurations,
                configuredTargets=configuredTargets,
            )
        else:
            AbstractPolicyManager.__logger.warn(
                "Policy rules MUST have a class name. Skipping policy rule with no class name defined..."
            )

        return configuredRule

    @property
    def policies(self) -> Dict[str, Policy]:
        """
        Method to return the policies that have been added to this manager.
        """
        return self._policies

    def getPolicy(self, policyIdentifier: str) -> Policy:
        """
        Method to get a policy with the given identifier.
        """
        policy = None
        if policyIdentifier.strip():
            policy = self._policies.get(policyIdentifier)

        return policy

    def getDeserializationClass(self) -> any:
        """
        Method that allows subclasses to override the type reference with a subclass.
        """
        return PolicyInput

    def createPolicy(self, policyIdentifier: str) -> Policy:
        """
        Method that allows subclasses to use an extended type for the policy class.
        """
        return Policy(identifier=policyIdentifier)

    def setAdditionalConfigurations(self, policy: Policy, input: PolicyInput) -> None:
        """
        Method that allows subclasses to set any additional configurations while reading a policy.
        """
        pass
