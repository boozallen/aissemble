# #%L
# aiSSEMBLE::Extensions::Transform::Spark::Python
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
# #L%
###
from typing import Dict
from krausening.logging import LogManager
from pyspark.sql.dataframe import DataFrame
from policy_manager.policy import ConfiguredRule
from data_transform_core import DataTransformException
from data_transform_core.mediator import MediationContext
from data_transform_core.policy import DataTransformPolicyManager, DataTransformPolicy


class DataTransformer:
    """
    DataTransformer class represents the rule engine for looking up a
    specified data transform policy, invoking the corresponding transformation
    rules, and then processing the results from rule invocation.
    """

    __logger = LogManager.get_instance().get_logger("DataTransformer")

    def __init__(self) -> None:
        self._policyManager = DataTransformPolicyManager.getInstance()

    def transform(self, policyIdentifier: str, dataset: DataFrame) -> DataFrame:
        """
        Applies a data transform policy on a dataset.
        """
        policy = self._policyManager.getPolicy(policyIdentifier)
        rules = policy.rules if policy else None

        if not policy:
            raise DataTransformException(
                'No data transform policy found for identifier "%s"' % policyIdentifier
            )

        if not rules:
            raise DataTransformException(
                'No rules found in data transform policy "%s"' % policyIdentifier
            )

        if len(rules) > 1:
            raise DataTransformException(
                'Data transform policy "%s" contains multiple rules, which is currently not supported'
                % policyIdentifier
            )

        return self.invokeMediator(policy, rules[0], dataset)

    def invokeMediator(
        self, policy: DataTransformPolicy, rule: ConfiguredRule, dataset: DataFrame
    ) -> DataFrame:
        inputType = rule.configurations["inputType"]
        outputType = rule.configurations["outputType"]

        if not inputType or not outputType:
            raise DataTransformException(
                'Rule inputType and/or outputType configuration is missing in data transform policy "%s"'
                % policy.identifier
            )

        # Look up the mediator to invoke
        mediationContext = MediationContext(inputType=inputType, outputType=outputType)
        mediator = policy.mediationManager.getMediator(mediationContext)

        DataTransformer.__logger.debug("Invoking mediator %s" % type(mediator))

        return mediator.mediate(dataset)

    def validate(self, input: any, properties: Dict[str, str]) -> None:
        """
        Validates the input and properties for this transformer.
        """
        if not isinstance(input, DataFrame):
            raise DataTransformException("Input (%s) is not a PySpark dataframe")
