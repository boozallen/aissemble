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
