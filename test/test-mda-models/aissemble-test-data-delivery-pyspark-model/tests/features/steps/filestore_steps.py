###
# #%L
# AIOps Foundation::AIOps MDA Patterns::Pyspark
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
# #L%
###
import os
import nose.tools as nt
from behave import given, when, then  # pylint: disable=no-name-in-module
from aissemble_core_filestore.file_store_factory import FileStoreFactory
from libcloud.utils.py3 import httplib
from krausening.logging import LogManager

logger = LogManager.get_instance().get_logger("filestore_steps.py")


def initialize(context):
    context.file_store = FileStoreFactory.create_file_store("S3Test")
    context.container = context.file_store.create_container("test-bucket")


def upload(context):
    return context.file_store.upload_object(
        "tests/resources/filestore/test.txt", context.container, "test.txt"
    )


@when("I provide valid credentials for the cloud provider")
def step_impl(context):
    initialize(context)
    context.response = context.file_store.connection.request("/")


@then("I am successfully logged in")
def step_impl(context):
    nt.ok_(
        context.response.status == httplib.OK,
        "File store not successfully authenticated",
    )


@when("I save an object")
def step_impl(context):
    initialize(context)
    context.response_obj = upload(context)


@then("the object is persisted")
def step_impl(context):
    nt.ok_(context.response_obj is not None, "Unable to persist object to storage")


@when("I request an object")
def step_impl(context):
    initialize(context)
    context.expected = upload(context)
    context.response = context.file_store.download_object(
        context.expected, "target/test.txt"
    )
    context.actual = context.file_store.get_object("test-bucket", "test.txt")


@then("the object is returned")
def step_impl(context):
    logger.warn(f"Context expected = {context.expected} and actual = {context.actual}")
    nt.ok_(context.response, "Unable to download the object")
    nt.ok_(
        context.expected.hash == context.actual.hash,
        "Unable to download the expected object",
    )
