###
# #%L
# aiSSEMBLE Data Encryption::Encryption (Python)
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
# #L%
###
"""
Configurations for aissemble-vault, read from the encrypt properties file or environment variables.
"""
from krausening.logging import LogManager
from krausening.properties import PropertyManager
import os
import requests
import time

logger = LogManager.get_instance().get_logger("VaultConfig")


class VaultConfig:
    """
    Configurations for aissemble-vault
    """

    def __init__(self):
        self.properties = PropertyManager.get_instance().get_properties(
            "encrypt.properties"
        )

    def secrets_root_key(self):
        """
        Returns the root key.
        """
        if "SECRETS_ROOT_KEY" in os.environ:
            # root key from the aissemble-vault container
            return os.environ["SECRETS_ROOT_KEY"]
        else:
            return self.properties["secrets.root.key"]

    def secrets_unseal_keys(self):
        """
        Returns the unseal keys.
        """
        if "SECRETS_UNSEAL_KEYS" in os.environ:
            # unseal keys from the aissemble-vault container
            return os.environ["SECRETS_UNSEAL_KEYS"]
        else:
            return self.properties["secrets.unseal.keys"]

    def encrypt_client_token(self):
        """
        Returns the client token.
        """
        if "ENCRYPT_CLIENT_TOKEN" in os.environ:
            # client token from the aissemble-vault container
            return os.environ["ENCRYPT_CLIENT_TOKEN"]
        else:
            return self.properties["encrypt.client.token"]

    def secrets_host_url(self):
        """
        Returns the secrets host url.
        This should only be used for integration tests, and not otherwise.
        """
        return self.properties["secrets.host.url"]

    @staticmethod
    def validate_container_start():
        started = False
        max_wait = 20
        wait = 0
        while wait < max_wait:
            logger.info("Waiting for Vault to start")

            try:
                requests.get("http://localhost:8200/v1/sys/health")
                logger.info("Vault started successfully!")
                started = True
                break
            except:
                time.sleep(1)
                wait += 1

        if not started:
            logger.error("Vault failed to start in time!")

        return started
