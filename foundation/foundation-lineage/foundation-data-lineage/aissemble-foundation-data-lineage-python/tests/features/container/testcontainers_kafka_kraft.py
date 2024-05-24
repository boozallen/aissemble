from kafka import KafkaConsumer
from kafka.errors import KafkaError, UnrecognizedBrokerVersion, NoBrokersAvailable

from container.safe_docker_container import SafeDockerContainer
from testcontainers.core.waiting_utils import wait_container_is_ready


class KafkaKraftContainer(SafeDockerContainer):
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
