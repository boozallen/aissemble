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
import base64
from krausening.properties import PropertyManager

import hashlib
import os
from cryptography.hazmat.primitives.ciphers import Cipher
from cryptography.hazmat.primitives.ciphers.algorithms import AES
from cryptography.hazmat.primitives.ciphers.modes import CBC


class AesCbcEncrypt(object):
    """
    Class for encrypting using the AES algorithm
    """

    def __init__(self):
        self.properties = PropertyManager.get_instance().get_properties(
            "encrypt.properties"
        )
        self.bs = AES.block_size
        self.aes_key = self.properties["encrypt.aes.secret.key.spec"]
        if self.aes_key:
            self.key = hashlib.sha256(self.aes_key.encode()).digest()
        else:
            raise KeyError(
                "Property encrypt.aes.secret.key.spec was not found in encrypt.properties"
            )

    def encrypt(self, raw):
        raw = self._pad(raw)

        # Initialization Vector
        iv = os.urandom(AES.block_size // 8)

        cipher = Cipher(AES(self.key), CBC(iv))
        encryptor = cipher.encryptor()
        encrypted = encryptor.update(raw.encode()) + encryptor.finalize()
        return base64.b64encode(iv + encrypted)

    def decrypt(self, enc):
        enc = base64.b64decode(enc)
        iv = enc[: AES.block_size // 8]
        cipher = Cipher(AES(self.key), CBC(iv))
        decryptor = cipher.decryptor()
        decrypted = decryptor.update(enc[AES.block_size // 8 :]) + decryptor.finalize()
        return self._unpad(decrypted.decode("utf-8"))

    def _pad(self, s):
        return s + (self.bs - len(s) % self.bs) * chr(self.bs - len(s) % self.bs)

    @staticmethod
    def _unpad(s):
        return s[: -ord(s[len(s) - 1 :])]
