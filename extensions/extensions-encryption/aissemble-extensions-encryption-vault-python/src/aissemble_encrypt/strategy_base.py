###
# #%L
# aiSSEMBLE Data Encryption::Encryption (Python)
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
# #L%
###
from abc import ABC


class StrategyBase(ABC):
    def __init__(self):
        pass

    def encrypt(self, data: str):
        pass

    def decrypt(self, cipher_text: str):
        pass
