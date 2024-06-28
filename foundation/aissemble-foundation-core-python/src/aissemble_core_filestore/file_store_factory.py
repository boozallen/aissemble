###
# #%L
# aiSSEMBLE Foundation::aiSSEMBLE Core (Python)
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
# #L%
###
import os
from libcloud.storage.base import StorageDriver
from libcloud.storage.providers import get_driver
from distutils.util import strtobool
from krausening.logging import LogManager
from libcloud.storage.types import Provider


class FileStoreFactory:
    """
    FileStore abstraction to integrate with cloud storage providers. Creates a configured instance of
    https://libcloud.readthedocs.io/en/stable/_modules/libcloud/storage/base.html#StorageDriver for the given
    provider value.
    """

    logger = LogManager.get_instance().get_logger("FileStoreFactory")

    def __init__(self):
        pass

    @staticmethod
    def create_file_store(name: str) -> StorageDriver:
        """
        Method to create and return a configured file store instance. Uses the given name as the prefix
        for all configurable environment variables. See documentation for details.
        :param name: the name of the file store
        """

        filtered = {
            key: value for (key, value) in os.environ.items() if (key.startswith(name))
        }
        provider = filtered[name + "_FS_PROVIDER"]
        cls = get_driver(provider)

        if Provider.LOCAL == provider:
            return FileStoreFactory.create_local_file_store(name, filtered, cls)
        elif Provider.S3 == provider:
            return FileStoreFactory.create_s3_file_store(name, filtered, provider)
        else:
            LogManager.get_instance().get_logger("FileStoreFactory").error(
                "Could not initialize a filestore prodiver, "
                "please provide a filestore type as described here: https://boozallen.github.io/aissemble/current/file-storage-details.html"
            )
            return None

    @staticmethod
    def create_local_file_store(name: str, filtered, cls) -> StorageDriver:
        return cls(filtered[name + "_FS_ACCESS_KEY_ID"])

    @staticmethod
    def create_s3_file_store(name: str, filtered, provider) -> StorageDriver:
        secret_id = filtered[name + "_FS_ACCESS_KEY_ID"]
        secret_key = filtered[name + "_FS_SECRET_ACCESS_KEY"]

        # Default values
        fs_secure = True
        fs_host = None
        fs_port = None
        fs_api_version = None
        fs_region = None

        if name + "_FS_SECURE" in filtered:
            fs_secure = strtobool(filtered[name + "_FS_SECURE"])
        if name + "_FS_HOST" in filtered:
            fs_host = filtered[name + "_FS_HOST"]
        if name + "_FS_PORT" in filtered:
            fs_port = int(filtered[name + "_FS_PORT"])
        if name + "_FS_API_VERSION" in filtered:
            fs_api_version = filtered[name + "_FS_API_VERSION"]
        if name + "_FS_REGION" in filtered:
            fs_region = filtered[name + "_FS_REGION"]

        cls = get_driver(provider)
        return cls(
            secret_id,
            secret_key,
            fs_secure,
            fs_host,
            fs_port,
            fs_api_version,
            fs_region,
        )
