###
# #%L
# aiSSEMBLE Data Encryption::Encryption (Python)
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
# #L%
###
from .strategy_base import StrategyBase
from krausening.logging import LogManager
from aissemble_encrypt.vault_encrypt import VaultEncrypt


class VaultLocalEncryptionStrategy(StrategyBase):
    """
    Applies the AES CBC algorithm
    """

    def __init__(self, key_version=None):
        self._logger = LogManager.get_instance().get_logger(
            "VaultLocalEncryptionStrategy"
        )
        super().__init__()
        self.__vault_encrypt = VaultEncrypt.get_instance(key_version)

    def encrypt(self, data: str):
        return self.__vault_encrypt.encrypt_local(data)

    def decrypt(self, cipher_text: str):
        return self.__vault_encrypt.decrypt_local(cipher_text)

    def get_encrypt_key_is_ready(self):
        """
        This method checks if the local vault key is ready for use
        """
        return self.__vault_encrypt.vault_key is not None
