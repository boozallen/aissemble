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
from pyspark.sql import SparkSession
from krausening.logging import LogManager
from testcontainers.compose import DockerCompose
from aissemble_test_data_delivery_pyspark_model.generated import environment_base

"""
Behave test environment setup to configure Spark for unit tests.
GENERATED STUB CODE - PLEASE ***DO*** MODIFY
Originally generated from: templates/data-delivery-pyspark/behave.environment.py.vm
"""

logger = LogManager.get_instance().get_logger("Environment")


def select_docker_compose_arch():
    machine_arch = platform.machine().lower()
    if machine_arch in ["arm64", "aarch64"]:
        return "docker-compose-arm64.yml"
    return "docker-compose.yml"


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


def before_feature(context, feature):
    if "integration" in feature.tags:
        test_staging_path = "target/generated-sources/docker-compose-files"
        logger.info(
            f"Starting services defined in Docker Compose file at {test_staging_path}"
        )

        compose = DockerCompose(test_staging_path, select_docker_compose_arch())
        context.docker_compose_containers = compose

        compose.start()
        wait_for_docker_url = "http://localhost:4566"
        logger.info(
            f"Waiting for Docker Compose services to start - waiting for a response from {wait_for_docker_url}"
        )
        compose.wait_for(wait_for_docker_url)

        root_key_tuple = context.docker_compose_containers.exec_in_container(
            ["cat", "/root_key.txt"], "vault"
        )
        secrets_root_key = root_key_tuple[0]
        os.environ["SECRETS_ROOT_KEY"] = secrets_root_key

        unseal_keys_tuple = context.docker_compose_containers.exec_in_container(
            ["cat", "/unseal_keys.txt"], "vault"
        )
        unseal_keys_txt = unseal_keys_tuple[0]
        unseal_keys_json = json.loads(unseal_keys_txt)
        secrets_unseal_keys = ",".join(unseal_keys_json)
        os.environ["SECRETS_UNSEAL_KEYS"] = secrets_unseal_keys

        transit_client_token_tuple = (
            context.docker_compose_containers.exec_in_container(
                ["cat", "/transit_client_token.txt"], "vault"
            )
        )
        transit_client_token_txt = transit_client_token_tuple[0]
        transit_client_token_json = json.loads(transit_client_token_txt)
        encrypt_client_token = transit_client_token_json["auth"]["client_token"]
        os.environ["ENCRYPT_CLIENT_TOKEN"] = encrypt_client_token


def after_feature(context, feature):
    if "integration" in feature.tags:
        logger.info("Stopping Docker Compose services")
        context.docker_compose_containers.stop()
