###
# #%L
# AIOps Foundation::AIOps MDA Patterns::Pyspark
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
# #L%
###
import os
import platform
import json
import packaging.version
from container.safe_docker_container import SafeDockerContainer
from importlib import metadata
from pyspark.sql import SparkSession
from krausening.logging import LogManager
from testcontainers.compose import DockerCompose
from testcontainers.core.waiting_utils import wait_for
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

    os.environ["S3Test_FS_PROVIDER"] = "s3"
    os.environ["S3Test_FS_ACCESS_KEY_ID"] = "000000000000"
    os.environ["S3Test_FS_SECRET_ACCESS_KEY"] = (
        "E3FF2839C048B25C084DEBE9B26995E310250568"
    )
    os.environ["S3Test_FS_SECURE"] = "False"
    os.environ["S3Test_FS_HOST"] = "localhost"
    os.environ["S3Test_FS_PORT"] = "4566"


def after_all(context):
    environment_base.cleanup()
    # Stop test spark session after all tests
    context.test_spark_session.stop()


def before_scenario(context, scenario):
    context.input_fields = None
    context.pipeline = None
    context.inbound = None
    context.fields_to_encrypt = None
    context.encrypted_dataset = None


def after_scenario(context, scenario):
    pass

def start_container(context, docker_image, feature):
    logger.info(f"Starting container: {docker_image}")
    context.test_container.with_bind_ports(4566, 4566)
    context.test_container.start()
    wait_for_docker_url = "http://localhost:4566"
    logger.info(
        f"Waiting for Docker Compose services to start - waiting for a response from {wait_for_docker_url}"
    )
    wait_for(wait_for_docker_url)


def before_feature(context, feature):
    if "integration" in feature.tags:
        logger.info("Starting Test container services")
        #piggyback test docker image
        docker_image = "ghcr.io/boozallen/aissemble-vault:"

        # append current version to docker image
        # pyproject.toml has a "version" property, e.g. version = "0.12.0.dev"
        # using major, minor, patch and -SNAPSHOT if dev
        version = metadata.version("aissemble-extensions-encryption-vault-python")
        docker_image += version_to_tag(version)

        context.test_container = SafeDockerContainer(docker_image)
        start_container(context, docker_image, feature)

        root_key_tuple = context.test_container.exec("cat /root_key.txt")
        secrets_root_key = root_key_tuple.output.decode()
        os.environ["SECRETS_ROOT_KEY"] = secrets_root_key

        unseal_keys_tuple = context.test_container.exec("cat /unseal_keys.txt")
        unseal_keys_txt = unseal_keys_tuple.output.decode()
        unseal_keys_json = json.loads(unseal_keys_txt)
        secrets_unseal_keys = ",".join(unseal_keys_json)
        os.environ["SECRETS_UNSEAL_KEYS"] = secrets_unseal_keys

        transit_client_token_tuple = context.test_container.exec(
            "cat /transit_client_token.txt"
        )
        transit_client_token_txt = transit_client_token_tuple.output.decode()
        transit_client_token_json = json.loads(transit_client_token_txt)
        encrypt_client_token = transit_client_token_json["auth"]["client_token"]
        os.environ["ENCRYPT_CLIENT_TOKEN"] = encrypt_client_token



def after_feature(context, feature):
    if "integration" in feature.tags:
        logger.info("Stopping Docker Compose services")
        context.test_container.stop()


def version_to_tag(version_str: str) -> str:
    """Convert a python version into a docker tag for the same version.

    Args:
        version_str (str): The version string to convert.
    Returns:
        str: The docker tag for the version.
    """
    version = packaging.version.parse(version_str)
    tag = version.base_version
    if version.pre:
        tag += "-" + "".join([str(x) for x in version.pre])
    if version.is_devrelease:
        tag += "-SNAPSHOT"
    return tag