###
# #%L
# aiSSEMBLE::Extensions::Data Delivery::Spark Py
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
# #L%
###
from pyspark.sql import SparkSession
import yaml
from krausening.logging import LogManager

logger = LogManager.get_instance().get_logger("SparkSessionManager")
UNSAFE_LINE_DELIMITER = "# Source:"


"""
Configures the default SparkSession based to align to settings specified in the SparkApplication YAML.  Meant
to allow unit testing with approximately the same configuration source used for production.

:param sparkapplication_path(str): Path to the SparkApplication YAML to be parsed.
:return A reference to the configured SparkSession.
"""


def create_standalone_spark_session(sparkapplication_path: str) -> SparkSession:
    spec_map = yaml.safe_load(clean_helm_chart(sparkapplication_path))
    builder = SparkSession.builder.appName(spec_map["metadata"]["name"])
    logger.info("Starting session creation...")
    builder = builder.master("local[*]")
    if "jars" in spec_map["spec"]["deps"]:
        builder = builder.config(
            "spark.jars", ",".join(spec_map["spec"]["deps"]["jars"])
        )
    if "packages" in spec_map["spec"]["deps"]:
        builder = builder.config(
            "spark.jars.packages", ",".join(spec_map["spec"]["deps"]["packages"])
        )
    if "excludePackages" in spec_map["spec"]["deps"]:
        builder = builder.config(
            "spark.jars.excludes", ",".join(spec_map["spec"]["deps"]["excludePackages"])
        )
    if "repositories" in spec_map["spec"]["deps"]:
        builder = builder.config(
            "spark.jars.repositories",
            ",".join(spec_map["spec"]["deps"]["repositories"]),
        )
    for key, value in spec_map["spec"]["sparkConf"].items():
        builder = builder.config(key, value)
    return builder.getOrCreate()


"""
Cleans out any warnings from the top of the generated helm chart that Helm may have written. These warnings
prevent the generated file from being a valid yaml file, so it needs to be cleaned up before being passed to
the parser.

:param sparkapplication_path(str): Path to the SparkApplication YAML to be parsed.
:return A string that contains the corrected yaml content
"""


def clean_helm_chart(sparkapplication_path: str):
    with open(sparkapplication_path, "r") as spec_file:
        lines = spec_file.read().splitlines()
        backup = lines.copy()

        found_unsafe = False
        for line in lines[:]:
            if line.find(UNSAFE_LINE_DELIMITER) > -1:
                found_unsafe = True
                break
            else:
                lines.remove(line)

        if found_unsafe:
            return "\n".join(lines)
        else:
            return "\n".join(backup)
