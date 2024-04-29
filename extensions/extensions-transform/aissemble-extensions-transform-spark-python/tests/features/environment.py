# #%L
# aiSSEMBLE::Extensions::Transform::Spark::Python
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
# #L%
###
from pyspark.sql import SparkSession


def before_all(context):
    # Set test spark session for all tests
    context.test_spark_session = (
        SparkSession.builder.appName("TestSparkSession")
        .master("local[*]")
        .config("spark.driver.host", "localhost")
        .getOrCreate()
    )


def after_all(context):
    # Stop test spark session after all tests
    context.test_spark_session.stop()
