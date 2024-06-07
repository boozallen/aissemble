###
# #%L
# aiSSEMBLE Data Encryption::Encryption (Python)
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
# #L%
###
from krausening.logging import LogManager
from container.safe_docker_container import SafeDockerContainer
from testcontainers.core.waiting_utils import wait_for
from aissemble_encrypt.vault_config import VaultConfig
from importlib import metadata
import packaging.version
import platform
import os
import json

logger = LogManager.get_instance().get_logger("Environment")


def before_all(context):
    pass


def after_all(context):
    pass


def before_scenario(context, scenario):
    print("Clearing context...")
    context.encrypted_value = None
    context.encryption_strategy = None


def after_scenario(context, scenario):
    pass


def select_krausening_extensions():
    machine_arch = platform.machine().lower()
    if machine_arch in ["arm64", "aarch64"]:
        os.environ["KRAUSENING_EXTENSIONS"] = "tests/resources/krausening/arm64"


def start_container(context, docker_image, feature):
    logger.info(f"Starting container: {docker_image}")
    context.test_container.with_bind_ports(8200, 8200)
    context.test_container.start()
    wait_for(VaultConfig.validate_container_start)


def before_feature(context, feature):
    logger.info("Starting Test container services")

    docker_image = "boozallen/aissemble-vault:"

    # append current version to docker image
    # pyproject.toml has a "version" property, e.g. version = "0.12.0.dev"
    # using major, minor, patch and -SNAPSHOT if dev
    version = metadata.version("aissemble-extensions-encryption-vault-python")
    docker_image += version_to_tag(version)
    base_docker_image = docker_image

    try:
        if platform.machine().lower() in ["arm64", "aarch64"]:
            docker_image += "-arm64"
        else:
            docker_image += "-amd64"

        context.test_container = SafeDockerContainer(docker_image)
        start_container(context, docker_image, feature)

    except:
        logger.info(
            f"Could not find container {docker_image} (with specific platform).  Trying without platform..."
        )
        context.test_container = SafeDockerContainer(base_docker_image)
        start_container(context, base_docker_image, feature)

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


# Execute docker-compose up/down if an @integration tag is encountered. Future iterations should
# iterate on this approach to potentially only launch the relevant Docker Compose services once
# per feature
def before_tag(context, tag):
    pass


def after_tag(context, tag):
    pass


def after_feature(context, feature):
    logger.info("Stopping Test container services")
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
