###
# #%L
# aiSSEMBLE Foundation::aiSSEMBLE Core (Python)
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
# #L%
###
from krausening.logging import LogManager
from .metadata_api import MetadataAPI
from .metadata_model import MetadataModel
from typing import Dict, List


class LoggingMetadataAPIService(MetadataAPI):
    """
    Class to handle basic logging of metadata. Intended for testing purposes and not suited for production.
    """

    logger = LogManager.get_instance().get_logger("LoggingMetadataAPIService")

    def create_metadata(self, metadata: MetadataModel) -> None:
        self.logger.warn(
            "Metadata being handled by default Logging implementation. "
            + "This is designed for testing and is not suited for production use-cases."
        )

        if metadata:
            message = "Metadata:"
            message += "\nSubject: " + metadata.subject
            message += "\nResource: " + metadata.resource
            message += "\nAction: " + metadata.action
            message += "\nTimestamp: " + metadata.timestamp.strftime(
                "%m/%d/%Y %H:%M:%S"
            )
            message += "\nAdditional Properties:"
            for key, value in metadata.additionalValues.items():
                message += "\n\t" + key + ": " + value

            self.logger.info(message)
        else:
            self.logger.warn("Attempting to create null metadata.")

    def get_metadata(self, search_params: Dict[str, any]) -> List[MetadataModel]:
        return []
