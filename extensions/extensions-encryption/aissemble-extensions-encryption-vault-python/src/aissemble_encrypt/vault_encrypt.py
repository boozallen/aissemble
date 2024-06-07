###
# #%L
# aiSSEMBLE Data Encryption::Encryption (Python)
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
# #L%
###
import sys
import hvac
import base64
from cryptography.hazmat.primitives.ciphers.aead import AESGCM
from cryptography.hazmat.primitives.ciphers.algorithms import AES
import os
from krausening.logging import LogManager
import gc
from aissemble_encrypt.vault_key_util import VaultKeyUtil
from aissemble_encrypt.vault_config import VaultConfig


class VaultEncrypt:
    """
    Class for encrypting using the Vault service
    """

    VAULT_BLOCK_SIZE = int(96 / 8)  # 96 bits
    __instance = None
    _config: VaultConfig

    @staticmethod
    def get_instance(key_version=None):
        if VaultEncrypt.__instance is None:
            VaultEncrypt(key_version)

        return VaultEncrypt.__instance

    def __init__(self, key_version: str):
        """
        Initializes the vault client, checks the server status and unseals the service.
        Also retrieves the encryption key by version - default key is "latest".
        """
        self._config = VaultConfig()

        if VaultEncrypt.__instance is None:
            VaultEncrypt.__instance = self

            self._logger = LogManager.get_instance().get_logger("VaultEncrypt")
            self.__client = hvac.Client(url=self._config.secrets_host_url())
            self.__client.token = self._config.secrets_root_key()
            self.bs = AES.block_size

            # Check if Vault service is running
            self.assert_server_status()

            # Unseal the vault if needed
            self.check_seal_status_and_unseal_if_necessary(self.__client)

            vault_key_util = VaultKeyUtil.get_instance(key_version)
            self.vault_key = vault_key_util.get_vault_key()
        else:
            raise Exception("Class is a singleton")

    def encrypt(self, data: str):
        """
        Uses the Vault encryption service to encrypt data
        """
        encrypt_data_response = self.__client.secrets.transit.encrypt_data(
            name="aissemblekey",
            plaintext=self.base64_encode(data),
        )

        ciphertext = encrypt_data_response["data"]["ciphertext"]

        return ciphertext

    def encrypt_local(self, data: str):
        """
        Uses a downloaded Vault encryption key to encrypt data locally
        """
        iv = os.urandom(self.VAULT_BLOCK_SIZE)
        aad = None

        encoded_data = AESGCM(self.vault_key).encrypt(iv, data.encode(), aad)
        data_decoded = encoded_data.decode("utf-8", "ignore")
        return base64.b64encode(iv + encoded_data)

    def decrypt_local(self, encrypted_data: str):
        """
        Uses a downloaded Vault encryption key to decrypt data locally
        """
        ciphertext = base64.b64decode(encrypted_data)

        iv = ciphertext[: self.VAULT_BLOCK_SIZE]  # First 96 bits
        actual_ciphertext = ciphertext[self.VAULT_BLOCK_SIZE :]  # Remaining bits
        aad = None

        plaintext = AESGCM(self.vault_key).decrypt(iv, actual_ciphertext, aad)

        return str(plaintext, "utf-8")

    def decrypt(self, encrypted_data: str):
        """
        Uses the Vault service to decrypt data
        """
        decrypt_data_response = self.__client.secrets.transit.decrypt_data(
            name="aissemblekey",
            ciphertext=encrypted_data,
        )

        plaintext = decrypt_data_response["data"]["plaintext"]

        return base64.b64decode(plaintext).decode("utf-8")

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

    def base64_encode(self, data):
        """Helper method to perform base64 encoding"""
        input_bytes = data.encode("utf8")
        base64_encoded = base64.urlsafe_b64encode(input_bytes)

        return base64_encoded.decode("ascii")

    def assert_server_status(self):
        is_server_ready = False
        server_status = ""
        try:
            is_server_ready = self.__client.sys.is_initialized()
        except Exception as e:
            server_status = e

        assert is_server_ready, server_status

    def __del__(self):
        if hasattr(self, "vault_key"):
            del self.vault_key
        gc.collect()
        self._logger.info("Clearing memory")
