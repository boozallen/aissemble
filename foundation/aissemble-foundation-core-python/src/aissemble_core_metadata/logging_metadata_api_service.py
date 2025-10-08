###
# #%L
# aiSSEMBLE Foundation::aiSSEMBLE Core (Python)
# %%
# Copyright (C) 2021 Booz Allen
# %%
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
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
