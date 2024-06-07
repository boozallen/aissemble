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
from aissemble_encrypt.aes_cbc_encrypt import AesCbcEncrypt


class AesCbcEncryptionStrategy(StrategyBase):
    """
    Applies the AES CBC algorithm
    """

    def __init__(self, key_version=None):
        self._logger = LogManager.get_instance().get_logger("AesCbcStrategy")
        super().__init__()
        self.aes_encrypt = AesCbcEncrypt()

    def encrypt(self, data: str):
        return self.aes_encrypt.encrypt(data)

    def decrypt(self, cipher_text: str):
        return self.aes_encrypt.decrypt(cipher_text)
