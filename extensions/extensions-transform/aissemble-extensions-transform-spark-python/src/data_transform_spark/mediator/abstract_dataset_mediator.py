###
# #%L
# aiSSEMBLE::Extensions::Transform::Spark::Python
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
# #L%
###
from abc import abstractmethod
from typing import Dict
from pyspark.sql.dataframe import DataFrame
from krausening.logging import LogManager
from data_transform_core import DataTransformException
from data_transform_core.mediator import Mediator


class AbstractDatasetMediator(Mediator):
    """
    AbstractDatasetMediator class represents a {@link Mediator} for
    performing transformations on a Spark dataset.
    """

    __logger = LogManager.get_instance().get_logger("AbstractDatasetMediator")

    def performMediation(self, input: any, properties: Dict[str, str]) -> any:
        output = None

        if input is not None:
            self.validate(input, properties)
            output = self.transform(input, properties)
        else:
            AbstractDatasetMediator.__logger.warn(
                "Input dataset is null - skipping transformation"
            )

        return output

    def validate(self, input: any, properties: Dict[str, str]) -> None:
        """
        Validates the input and properties for this transformer.
        """
        if not isinstance(input, DataFrame):
            raise DataTransformException("Input (%s) is not a PySpark dataframe")

    @abstractmethod
    def transform(self, input: DataFrame, properties: Dict[str, str]) -> DataFrame:
        """
        Transforms the input dataset.
        """
        pass
