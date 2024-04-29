###
# #%L
# aiSSEMBLE Data Encryption::Encryption (Python)
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
# #L%
###
import os

from behave import *
import nose.tools as nt
from aiops_encrypt.aes_cbc_encryption_strategy import AesCbcEncryptionStrategy
from aiops_encrypt.aes_gcm_96_encryption_strategy import AesGcm96EncryptionStrategy
from aiops_encrypt.vault_remote_encryption_strategy import VaultRemoteEncryptionStrategy
from aiops_encrypt.vault_local_encryption_strategy import VaultLocalEncryptionStrategy
from aiops_encrypt.vault_key_util import VaultKeyUtil
from krausening.logging import LogManager
from json import dumps

use_step_matcher("re")

original_value = "123-4567-890"

logger = LogManager.get_instance().get_logger("EncryptionTests")


@given("a plain text string")
def step_impl(context):
    os.environ["KRAUSENING_BASE"] = "./tests/resources/krausening/base"


@when("the string is encrypted using Vault")
def step_impl(context):
    context.encryption_strategy = VaultRemoteEncryptionStrategy()

    context.encrypted_value = context.encryption_strategy.encrypt(original_value)

    nt.ok_(
        context.encrypted_value != original_value,
        "Encrypted value was the same as the original",
    )
    logger.info("========= Vault server encryption =========")
    logger.info(context.encrypted_value)


@when("the string is encrypted using AES encryption")
def step_impl(context):
    aes_cbc_strategy = AesCbcEncryptionStrategy()
    context.encrypted_value = aes_cbc_strategy.encrypt(original_value)

    nt.ok_(
        context.encrypted_value != original_value,
        "Encrypted value was the same as the original",
    )
    logger.info("========= AES encryption =========")
    logger.info(context.encrypted_value)


@when("local vault encryption is requested")
def step_impl(context):
    logger.info("========= Local Vault encryption =========")
    context.encryption_strategy = VaultLocalEncryptionStrategy()
    context.encrypted_value = context.encryption_strategy.encrypt(original_value)
    logger.info("Encrypted value: " + str(context.encrypted_value, "utf-8"))
    nt.ok_(context.encrypted_value is not None, "Encrypted value was None")
    nt.ok_(
        context.encrypted_value != original_value,
        "Encrypted value was the same as the original",
    )


@then("the encrypted string can be decrypted using Vault")
def step_impl(context):
    decrypted_value = context.encryption_strategy.decrypt(context.encrypted_value)
    nt.ok_(
        decrypted_value == original_value,
        "Decrypted value was not the same as the original",
    )
    logger.info("Decrypted value: " + decrypted_value)


@then("the encrypted string can be decrypted using AES")
def step_impl(context):
    aes_cbc_strategy = AesCbcEncryptionStrategy()
    decrypted_value = aes_cbc_strategy.decrypt(context.encrypted_value)
    nt.ok_(
        decrypted_value == original_value,
        "Decrypted value was not the same as the original",
    )
    logger.info("Decrypted value: " + decrypted_value)


@then("a key is downloaded from the Vault server")
def step_impl(context):
    nt.ok_(
        context.encryption_strategy.get_encrypt_key_is_ready(),
        "Local encryption did not download a key from the server",
    )


@then("the encrypted data can be decrypted using the local key copy")
def step_impl(context):
    decrypted_value = context.encryption_strategy.decrypt(context.encrypted_value)
    nt.ok_(
        decrypted_value == original_value,
        "Decrypted value was not the same as the original",
    )
    logger.info("Decrypted value: " + decrypted_value)


@when("the string is encrypted using AES GCM 96 encryption")
def step_impl(context):
    vault_key_util = VaultKeyUtil.get_instance()
    vault_key = vault_key_util.get_vault_key_encoded()

    context.encryption_strategy = AesGcm96EncryptionStrategy(vault_key)
    context.encrypted_value = context.encryption_strategy.encrypt(original_value)

    nt.ok_(
        context.encrypted_value != original_value,
        "Encrypted value was the same as the original",
    )
    logger.info("========= AES GCM 96 encryption (Local Vault) =========")
    logger.info(context.encrypted_value)


@then("the encrypted string can be decrypted using AES GCM 96")
def step_impl(context):
    decrypted_value = context.encryption_strategy.decrypt(context.encrypted_value)
    nt.ok_(
        decrypted_value == original_value,
        "Decrypted value was not the same as the original",
    )
    logger.info("Decrypted value: " + decrypted_value)
