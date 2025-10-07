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
from typing import Dict
from pyspark.sql.dataframe import DataFrame
from krausening.logging import LogManager
from data_transform_spark.mediator import AbstractDatasetMediator


class DatasetLogger(AbstractDatasetMediator):
    """
    DatasetLogger class is an example AbstractDatasetMediator
    that logs a sample of a dataset and its schema.
    """

    __logger = LogManager.get_instance().get_logger("DatasetLogger")

    def transform(self, input: DataFrame, properties: Dict[str, str]) -> DataFrame:
        sampleString = input._jdf.showString(5, 0, False)
        DatasetLogger.__logger.info(
            "Below is a sample of the input data:\n%s" % sampleString
        )

        schemaString = input._jdf.schema().treeString()
        DatasetLogger.__logger.info("With the following schema:\n%s" % schemaString)

        return input
