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
