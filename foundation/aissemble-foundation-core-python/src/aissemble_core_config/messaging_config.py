###
# #%L
# aiSSEMBLE Foundation::aiSSEMBLE Core (Python)
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
# #L%
###
from krausening.properties import PropertyManager


class MessagingConfig:
    """
    Configurations for messaging connections
    """

    def __init__(self) -> None:
        self.properties = PropertyManager.get_instance().get_properties(
            "messaging.properties"
        )

    def server(self) -> str:
        """
        Server address
        """
        return self.properties.getProperty("server", "kafka-cluster:9093")

    def metadata_topic(self) -> str:
        """
        Topic for metadata
        """
        return self.properties.getProperty("metadata_topic", "metadata-ingest")
