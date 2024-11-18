###
# #%L
# AIOps Foundation::AIOps MDA Patterns::Pyspark
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
# #L%
###
"""
Implementation steps for pyspark_data_delivery.feature.

GENERATED STUB CODE - PLEASE ***DO*** MODIFY

Originally generated from: templates/behave.steps.py.vm
"""

from behave import given, when, then  # pylint: disable=no-name-in-module
from os import path, walk
import nose.tools as nt
import ast
import sys
import os
from pathlib import Path
from aissemble_test_data_delivery_pyspark_model.step.native_inbound_with_custom_types import (
    NativeInboundWithCustomTypes,
)
from aissemble_test_data_delivery_pyspark_model.step.native_inbound_and_messaging_outbound import (
    NativeInboundAndMessagingOutbound,
)
from aissemble_test_data_delivery_pyspark_model.step.native_inbound_with_custom_collection_type import (
    NativeInboundWithCustomCollectionType,
)
from aissemble_test_data_delivery_pyspark_model.step.native_inbound_with_custom_data_type_async import (
    NativeInboundWithCustomDataTypeAsync,
)
from aissemble_test_data_delivery_pyspark_model.record.custom_data import CustomData
from krausening.logging import LogManager
from pyspark.sql.types import StructType, StructField, StringType

logger = LogManager.get_instance().get_logger("DataEncryptionTest")


@given("a pipeline with native inbound collection and inbound record type")
def step_impl(context):
    custom_data1 = CustomData()
    custom_data1.custom_field = "some test data"
    context.inbound = {custom_data1}

    context.fields_to_encrypt = ["custom_field"]

    context.pipeline = NativeInboundWithCustomTypes()
    nt.ok_(
        context.pipeline is not None,
        "Could not create a NativeInboundWithCustomTypesAsync",
    )


@given("a pipeline with native non-collection inbound and no inbound record type")
def step_impl(context):
    context.inbound = context.test_spark_session.createDataFrame(
        [("some test data",)], ["custom_field"]
    )
    context.inbound.printSchema()
    context.fields_to_encrypt = ["custom_field"]

    context.pipeline = NativeInboundAndMessagingOutbound()
    nt.ok_(
        context.pipeline is not None,
        "Could not create a NativeInboundAndMessagingOutbound",
    )


@given("a pipeline with native collection inbound and no inbound record type")
def step_impl(context):
    df1 = context.test_spark_session.createDataFrame(
        [("some test data",)], ["custom_field"]
    )

    df2 = context.test_spark_session.createDataFrame(
        [("some other test data",)], ["custom_field"]
    )

    context.inbound = set([df1, df2])

    context.fields_to_encrypt = ["custom_field"]

    context.pipeline = NativeInboundWithCustomCollectionType()
    nt.ok_(
        context.pipeline is not None,
        "Could not create a NativeInboundWithCustomCollectionType",
    )


@when("field names are retrieved for the inbound record")
def step_impl(context):
    context.input_fields = context.pipeline.get_fields_list(context.inbound)
    logger.info(context.input_fields)
    nt.eq_(len(context.input_fields), 1, "Wrong number of input fields")


@when("field names are retrieved for the set of inbound dataframes")
def step_impl(context):
    for df in context.inbound:
        context.input_fields = context.pipeline.get_fields_list(df)
        logger.info(context.input_fields)
        nt.eq_(len(context.input_fields), 1, "Wrong number of input fields")


@when("encryption is called on the inbound record")
def step_impl(context):
    context.encrypted_dataset = context.pipeline.apply_encryption_to_dataset(
        context.inbound, context.fields_to_encrypt, "AES"
    )
    logger.info("processing encrypted_dataset")


@when("encryption is called on the native data set")
def step_impl(context):
    context.encrypted_dataset = context.pipeline.apply_encryption_to_dataset(
        context.inbound, context.fields_to_encrypt, "AES"
    )
    logger.info("processing encrypted_dataset")

    logger.info(context.encrypted_dataset)


@then("a list of field names can be retrieved")
def step_impl(context):
    nt.eq_(
        context.input_fields[0],
        "custom_field",
        'Input field was not "custom_field".  Instead it was '
        + context.input_fields[0],
    )


@then("the correct fields are encrypted")
def step_impl(context):
    for record in context.encrypted_dataset:
        nt.ok_(
            record.custom_field != "some test data",
            "Field data was not encrypted.  Still the original value.",
        )
        nt.ok_(
            len(record.custom_field) > 32,
            "Field data size was not consistent with an encrypted value.",
        )


@then("each list of field names can be retrieved")
def step_impl(context):
    nt.eq_(
        context.input_fields[0],
        "custom_field",
        'Input field was not "custom_field".  Instead it was '
        + context.input_fields[0],
    )


@then("the correct dataframe fields are encrypted")
def step_impl(context):
    encrypted_field = context.encrypted_dataset.collect()[0][0]

    logger.info(
        "DataFrame Encrypted Value: " + context.encrypted_dataset.collect()[0][0]
    )
    nt.ok_(
        encrypted_field != "some test data",
        "Field data was not encrypted.  Still the original value.",
    )
    nt.ok_(
        len(encrypted_field) > 32,
        "Field data size was not consistent with an encrypted value.",
    )


@then("the correct dataframe fields are encrypted for each dataframe")
def step_impl(context):
    for df in context.encrypted_dataset:
        encrypted_field = df.collect()[0][0]
        logger.info("Set[(dataframe)] - Encrypted Value: " + encrypted_field)
        nt.ok_(
            encrypted_field != "some test data",
            "Field data from set was not encrypted.  Still the original value.",
        )
        nt.ok_(
            len(encrypted_field) > 32,
            "Field data size from set was not consistent with an encrypted value.",
        )


@when("AES encryption is requested")
def step_impl(context):
    context.encrypted_dataset = context.pipeline.apply_encryption_to_dataset(
        context.inbound, context.fields_to_encrypt, "AES"
    )


@then("the correct AES algorithm is applied to the data set")
def step_impl(context):
    for record in context.encrypted_dataset:
        logger.info("Set[(CustomData)] - AES Encrypted Value: " + record.custom_field)
        nt.ok_(
            record.custom_field != "some test data",
            "Field data from set was not AES encrypted.  Still the original value.",
        )
        nt.ok_(
            len(record.custom_field) > 32,
            "Field data size from set was not consistent with an AES encrypted value.",
        )


@when("Vault encryption is requested")
def step_impl(context):
    context.encrypted_dataset = context.pipeline.apply_encryption_to_dataset(
        context.inbound, context.fields_to_encrypt, "VAULT_ENCRYPT"
    )


@then("the correct Vault algorithm is applied to the data set")
def step_impl(context):
    for record in context.encrypted_dataset:
        nt.ok_(
            record.custom_field.startswith("vault:"),
            "Field data from set was not encrypted using Vault.",
        )
        logger.info("CustomData - The Vault encrypted value is: " + record.custom_field)


@then("the correct Vault algorithm is applied to the dataframe")
def step_impl(context):
    encrypted_field = context.encrypted_dataset.collect()[0][0]

    nt.ok_(
        encrypted_field != "some test data",
        "Field data was not encrypted.  Still the original value.",
    )
    # Local Vault encryption uses AES GCM 96 with the provided encryption key
    nt.ok_(
        len(encrypted_field) > 32,
        "Field data size was not consistent with an encrypted value.",
    )
    logger.info("dataframe - The Vault encrypted value is: " + encrypted_field)


@then("the correct dataframe fields are vault encrypted for each dataframe")
def step_impl(context):
    for df in context.encrypted_dataset:
        encrypted_field = df.collect()[0][0]

        nt.ok_(
            encrypted_field != "some test data",
            "Field data was not encrypted.  Still the original value.",
        )
        # Local Vault encryption uses AES GCM 96 with the provided encryption key
        nt.ok_(
            len(encrypted_field) > 32,
            "Field data size was not consistent with an encrypted value.",
        )
        logger.info(
            "Set[(dataframe)] - The Vault encrypted value is: " + encrypted_field
        )
