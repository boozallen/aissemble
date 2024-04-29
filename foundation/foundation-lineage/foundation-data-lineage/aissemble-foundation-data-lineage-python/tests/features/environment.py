from krausening.logging import LogManager
from krausening.properties import PropertyManager
import os
from container.testcontainers_kafka_kraft import KafkaKraftContainer

logger = LogManager.get_instance().get_logger(__name__)


def before_scenario(context, scenario):
    context.service_port = None
    context.emitter = None
    os.environ["KRAUSENING_BASE"] = os.path.join("target/krausening/base")
    context.lineage_properties = PropertyManager.get_instance().get_properties(
        "data-lineage.properties", force_reload=True
    )

    if "kafka" in scenario.tags:
        container = KafkaKraftContainer(port=9092)
        container.with_bind_ports(9092, 9092)
        container.start()
        context.container = container
        context.kafka_port = container.get_exposed_port(9092)
        os.environ["kafka.bootstrap.servers"] = f"localhost:{context.kafka_port}"


def after_scenario(context, scenario):
    if "kafka" in scenario.tags:
        context.container.stop()
