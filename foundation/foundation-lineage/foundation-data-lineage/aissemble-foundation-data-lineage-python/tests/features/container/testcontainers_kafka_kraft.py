###
# #%L
# aiSSEMBLE::Foundation::Data Lineage::Python
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
from kafka import KafkaConsumer
from kafka.errors import KafkaError, UnrecognizedBrokerVersion, NoBrokersAvailable

from testcontainers.core.container import DockerContainer
from testcontainers.core.waiting_utils import wait_container_is_ready


class KafkaKraftContainer(DockerContainer):
    """
    Kafka container.

    Example:

        .. doctest::

            >>> from testcontainers.kafka import KafkaKraftContainer

            >>> with KafkaKraftContainer() as kafka:
            ...    connection = kafka.get_bootstrap_server()
    """

    def __init__(
        self, image: str = "bashj79/kafka-kraft:3.0.0", port: int = 9093, **kwargs
    ) -> None:
        super(KafkaKraftContainer, self).__init__(image, **kwargs)
        self.port = port
        self.with_exposed_ports(self.port)

    def get_bootstrap_server(self) -> str:
        host = self.get_container_host_ip()
        port = self.get_exposed_port(self.port)
        return f"{host}:{port}"

    @wait_container_is_ready(
        UnrecognizedBrokerVersion, NoBrokersAvailable, KafkaError, ValueError
    )
    def _connect(self) -> None:
        bootstrap_server = self.get_bootstrap_server()
        consumer = KafkaConsumer(group_id="test", bootstrap_servers=[bootstrap_server])
        if not consumer.bootstrap_connected():
            raise KafkaError("Unable to connect with kafka container!")

    def start(self) -> "KafkaContainer":
        super().start()
        self._connect()
        return self
