###
# #%L
# AIOps Foundation::AIOps MDA Patterns::Pyspark
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
import os
import json
import time
from importlib import metadata
from pyspark.sql import SparkSession
from krausening.logging import LogManager
from testcontainers.core.container import DockerContainer
from aissemble_test_data_delivery_pyspark_model.generated import environment_base

"""
Behave test environment setup to configure Spark for unit tests.
GENERATED STUB CODE - PLEASE ***DO*** MODIFY
Originally generated from: templates/data-delivery-pyspark/behave.environment.py.vm
"""

logger = LogManager.get_instance().get_logger("Environment")


def before_all(context):
    print("Executing setup for tests...")
    os.environ["KRAUSENING_BASE"] = "tests/resources/config/base"
    environment_base.initialize()
    # Set test spark session for all tests
    context.test_spark_session = SparkSession.builder.getOrCreate()

    print("Created spark session for tests...")


def after_all(context):
    environment_base.cleanup()
    # Stop test spark session after all tests
    context.test_spark_session.stop()


def before_feature(context, feature):
    if "integration" in feature.tags:
        logger.info("Starting Test container services")
        context.test_containers = []
        setup_s3_local(context)


def setup_s3_local(context):
    localstack = DockerContainer("localstack/localstack:latest")
    localstack.with_env("SERVICES", "s3")
    port = start_container(localstack, 4566, lambda _: test_aws(localstack))
    context.test_containers.append(localstack)
    os.environ["S3Test_FS_PROVIDER"] = "s3"
    os.environ["S3Test_FS_ACCESS_KEY_ID"] = "000000000000"
    os.environ["S3Test_FS_SECRET_ACCESS_KEY"] = (
        "E3FF2839C048B25C084DEBE9B26995E310250568"
    )
    os.environ["S3Test_FS_SECURE"] = "False"
    os.environ["S3Test_FS_HOST"] = "localhost"
    os.environ["S3Test_FS_PORT"] = f"{port}"


def after_feature(context, feature):
    if hasattr(context, "test_containers"):
        logger.info("Stopping Test container services")
        for container in context.test_containers:
            logger.info(f"...stopping {container.image}")
            container.stop()


def start_container(container, port, healthcheck=lambda x: True) -> int:
    logger.info(f"Starting container: {container.image}")
    container.with_exposed_ports(port)
    container.start()
    extport = container.get_exposed_port(port)
    if not healthcheck(extport):
        raise Exception(f"Failed to start {container.image}")
    return extport


def test_aws(container):
    started = False
    tries = 0
    exitcode = -1
    while exitcode != 0 and tries < 20:
        logger.info("Waiting for s3 to start...")
        tries += 1
        exitcode, _ = container.exec(
            "bash -c 'AWS_ACCESS_KEY_ID=fake AWS_SECRET_ACCESS_KEY=fake aws --endpoint-url=http://localhost:4566 s3 ls'"
        )
        if exitcode == 0:
            started = True
        else:
            time.sleep(1)

    return started
