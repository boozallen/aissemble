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
from aissemble_core_config import MessagingConfig
from .metadata_api import MetadataAPI
from .metadata_model import MetadataModel
from typing import Dict, List
from kafka import KafkaProducer
import json
import jsonpickle


class HiveMetadataAPIService(MetadataAPI):
    """
    Class to handle basic logging of metadata.
    """

    logger = LogManager.get_instance().get_logger("HiveMetadataAPIService")

    def __init__(self):
        self.config = MessagingConfig()
        self.producer = KafkaProducer(
            bootstrap_servers=[self.config.server()], api_version=(2, 0, 2)
        )

    def create_metadata(self, metadata: MetadataModel) -> None:
        if metadata:
            out = jsonpickle.encode(metadata.dict()).encode()
            self.producer.send(self.config.metadata_topic(), value=out)
        else:
            HiveMetadataAPIService.logger.warn("Attempting to create null metadata.")

    def get_metadata(self, search_params: Dict[str, any]) -> List[MetadataModel]:
        return []
