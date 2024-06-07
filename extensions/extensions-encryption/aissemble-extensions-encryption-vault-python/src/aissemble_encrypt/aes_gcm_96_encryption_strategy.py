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
from aissemble_encrypt.aes_gcm_96_encrypt import AesGcm96Encrypt


class AesGcm96EncryptionStrategy(StrategyBase):
    """
    Applies the AES GCM algorithm
    """

    def __init__(self, key=None):
        self._logger = LogManager.get_instance().get_logger(
            "AesGcm96EncryptionStrategy"
        )
        super().__init__()
        self.__aes_gcm_96_encrypt = AesGcm96Encrypt.get_instance(key)

    def encrypt(self, data: str):
        return self.__aes_gcm_96_encrypt.encrypt(data)

    def decrypt(self, cipher_text: str):
        return self.__aes_gcm_96_encrypt.decrypt(cipher_text)
