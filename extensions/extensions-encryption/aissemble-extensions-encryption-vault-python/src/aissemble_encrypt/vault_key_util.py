###
# #%L
# aiSSEMBLE Data Encryption::Encryption (Python)
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
# #L%
###
import hvac
import base64
from krausening.logging import LogManager
import gc
from aissemble_encrypt.vault_config import VaultConfig


class VaultKeyUtil:
    """
    Class for encrypting using the Vault service
    """

    VAULT_BLOCK_SIZE = int(96 / 8)  # 96 bits
    __instance = None
    _config: VaultConfig

    @staticmethod
    def get_instance(key_version=None):
        if VaultKeyUtil.__instance is None:
            VaultKeyUtil(key_version)

        return VaultKeyUtil.__instance

    def __init__(self, key_version: str):
        """
        Initializes the vault client, checks the server status and unseals the service.
        Also retrieves the encryption key by version - default key is "latest".
        """
        self._config = VaultConfig()

        if VaultKeyUtil.__instance is None:
            VaultKeyUtil.__instance = self

            self._logger = LogManager.get_instance().get_logger("VaultKeyUtil")
            self.__client = hvac.Client(url=self._config.secrets_host_url())
            self.__client.token = self._config.secrets_root_key()

            # Check if Vault service is running
            self.assert_server_status()

            # Unseal the vault if needed
            self.check_seal_status_and_unseal_if_necessary(self.__client)

            exported_key = self.__get_key_export(key_version)
            self.vault_key = base64.b64decode(exported_key)
        else:
            raise Exception("Class is a singleton")

    def __get_key_export(self, key_version="latest"):
        export_key_response = self.__client.secrets.transit.export_key(
            name="aissemblekey",
            key_type="encryption-key",
            version=key_version,
        )

        exported_keys = export_key_response["data"]["keys"]

        assert exported_keys is not None, "Could not retrieve Vault keys"

        return list(exported_keys.values())[0]

    def get_vault_key(self):
        return self.vault_key

    def get_vault_key_encoded(self):
        return base64.b64encode(self.vault_key).decode("ascii")

    def check_seal_status_and_unseal_if_necessary(self, client):
        """
        Checks the status of the Vault server.  If it is sealed then unseal it
        with the keys found in the property file
        """
        unseal_property = self._config.secrets_unseal_keys()
        if client.sys.is_sealed():
            if unseal_property:
                keys = unseal_property.split(",")

                # Unseal the vault
                for key in keys:
                    client.sys.submit_unseal_key(key)
                    if not client.sys.is_sealed():
                        break
            else:
                raise RuntimeError(
                    "Vault is sealed and unseal keys are not in the properties file or environment variable."
                )
        if client.sys.is_sealed():
            raise RuntimeError("Could not unseal vault.")

    def assert_server_status(self):
        is_server_ready = False
        server_status = ""
        try:
            is_server_ready = self.__client.sys.is_initialized()
        except Exception as e:
            server_status = e

        assert is_server_ready, server_status

    def base64_encode(self, data):
        """Helper method to perform base64 encoding"""
        input_bytes = data.encode("utf8")
        base64_encoded = base64.urlsafe_b64encode(input_bytes)

        return base64_encoded.decode("ascii")

    def __del__(self):
        if hasattr(self, "vault_key"):
            del self.vault_key
        gc.collect()
        self._logger.info("Clearing memory")
