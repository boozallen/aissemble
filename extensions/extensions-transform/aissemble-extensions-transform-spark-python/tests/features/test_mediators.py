# #%L
# aiSSEMBLE::Extensions::Transform::Spark::Python
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
# #L%
###
from data_transform_spark.mediator import AbstractDatasetMediator
from pyspark.sql.dataframe import DataFrame
from pyspark.sql.types import IntegerType
from krausening.logging import LogManager
from typing import Dict


class TestDatasetMediator(AbstractDatasetMediator):
    __logger = LogManager.get_instance().get_logger("TestDatasetMediator")
    DATA_TYPE = IntegerType()
    COLUMN = "testColumn"

    def transform(self, input: DataFrame, properties: Dict[str, str]) -> DataFrame:
        schemaString = input._jdf.schema().treeString()
        datasetString = input._jdf.showString(5, 0, False)
        TestDatasetMediator.__logger.info(
            "Dataset before transformation:\n%s\n%s" % (schemaString, datasetString)
        )

        output = input.withColumn(
            TestDatasetMediator.COLUMN,
            input[TestDatasetMediator.COLUMN].cast(TestDatasetMediator.DATA_TYPE),
        )

        schemaString = output._jdf.schema().treeString()
        datasetString = output._jdf.showString(5, 0, False)
        TestDatasetMediator.__logger.info(
            "Dataset after transformation:\n%s\n%s" % (schemaString, datasetString)
        )

        return output
