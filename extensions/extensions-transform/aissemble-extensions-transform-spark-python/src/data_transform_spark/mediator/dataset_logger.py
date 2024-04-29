# #%L
# aiSSEMBLE::Extensions::Transform::Spark::Python
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
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
