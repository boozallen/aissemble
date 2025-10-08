###
# #%L
# aiSSEMBLE::Extensions::Transform::Spark::Python
# %%
# Copyright (C) 2021 Booz Allen
# %%
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
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
