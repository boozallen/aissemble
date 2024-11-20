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
import json
import time
import packaging.version
from importlib import metadata
from pyspark.sql import SparkSession
from krausening.logging import LogManager
from testcontainers.core.container import DockerContainer
from aissemble_test_data_delivery_pyspark_model.generated import environment_base
from aissemble_encrypt.vault_config import VaultConfig

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
        logger.info("Starting Test container services")
        context.test_containers = []
        setup_vault(context)
        setup_s3_local(context)


def setup_vault(context):
    docker_image = "ghcr.io/boozallen/aissemble-vault:"
    # append current version to docker image
    # pyproject.toml has a "version" property, e.g. version = "0.12.0.dev"
    # using major, minor, patch and -SNAPSHOT if dev
    version = metadata.version("aissemble-extensions-encryption-vault-python")
    docker_image += version_to_tag(version)
    vault = DockerContainer(docker_image)
    port = start_container(vault, 8200, VaultConfig.validate_container_start)
    context.test_containers.append(vault)
    os.environ["SECRETS_HOST_URL"] = f"http://127.0.0.1:{port}"

    root_key_tuple = vault.exec("cat /root_key.txt")
    secrets_root_key = root_key_tuple.output.decode()
    os.environ["SECRETS_ROOT_KEY"] = secrets_root_key

    unseal_keys_tuple = vault.exec("cat /unseal_keys.txt")
    unseal_keys_txt = unseal_keys_tuple.output.decode()
    unseal_keys_json = json.loads(unseal_keys_txt)
    secrets_unseal_keys = ",".join(unseal_keys_json)
    os.environ["SECRETS_UNSEAL_KEYS"] = secrets_unseal_keys

    transit_client_token_tuple = vault.exec("cat /transit_client_token.txt")
    transit_client_token_txt = transit_client_token_tuple.output.decode()
    transit_client_token_json = json.loads(transit_client_token_txt)
    encrypt_client_token = transit_client_token_json["auth"]["client_token"]
    os.environ["ENCRYPT_CLIENT_TOKEN"] = encrypt_client_token


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
