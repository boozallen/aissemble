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


class VaultRemoteEncryptionStrategy(StrategyBase):
    """
    Applies encryption through remote calls to the Vault server
    """

    def __init__(self, key_version=None):
        self._logger = LogManager.get_instance().get_logger(
            "VaultRemoteEncryptionStrategy"
        )
        super().__init__()
        self.__vault_encrypt = VaultEncrypt.get_instance(key_version)

    def encrypt(self, data: str):
        return self.__vault_encrypt.encrypt(data)

    def decrypt(self, cipher_text: str):
        return self.__vault_encrypt.decrypt(cipher_text)
