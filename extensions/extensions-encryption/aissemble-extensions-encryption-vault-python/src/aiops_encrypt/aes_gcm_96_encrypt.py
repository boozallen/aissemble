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
from krausening.properties import PropertyManager
from cryptography.hazmat.primitives.ciphers.aead import AESGCM
import os
from krausening.logging import LogManager
import gc


class AesGcm96Encrypt:
    """
    Class for encrypting using the Vault service
    """

    VAULT_BLOCK_SIZE = int(96 / 8)  # 96 bits
    __instance = None

    @staticmethod
    def get_instance(key=None):
        if AesGcm96Encrypt.__instance is None:
            AesGcm96Encrypt(key)

        return AesGcm96Encrypt.__instance

    def __init__(self, key: str):
        """
        Initializes the key and properties.
        """
        if AesGcm96Encrypt.__instance is None:
            AesGcm96Encrypt.__instance = self

            self._logger = LogManager.get_instance().get_logger("AesGcm96Encrypt")
            self.properties = PropertyManager.get_instance().get_properties(
                "encrypt.properties"
            )

            self.aes_key = base64.b64decode(key)
        else:
            raise Exception("Class is a singleton")

    def encrypt(self, data: str):
        """
        Uses the supplied encryption key to encrypt data locally
        """
        iv = os.urandom(self.VAULT_BLOCK_SIZE)
        aad = None

        encoded_data = AESGCM(self.aes_key).encrypt(iv, data.encode(), aad)
        data_decoded = encoded_data.decode("utf-8", "ignore")
        return base64.b64encode(iv + encoded_data)

    def decrypt(self, encrypted_data: str):
        """
        Uses the supplied encryption key to decrypt data locally
        """
        ciphertext = base64.b64decode(encrypted_data)

        iv = ciphertext[: self.VAULT_BLOCK_SIZE]  # First 96 bits
        actual_ciphertext = ciphertext[self.VAULT_BLOCK_SIZE :]  # Remaining bits
        aad = None

        plaintext = AESGCM(self.aes_key).decrypt(iv, actual_ciphertext, aad)

        return str(plaintext, "utf-8")

    def __del__(self):
        del self.aes_key
        gc.collect()
        self._logger.info("Clearing memory")
