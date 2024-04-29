import logging
from uuid import uuid4
from time import sleep

from aissemble_data_lineage import *
from kafka import KafkaConsumer
from behave import *
import nose.tools as nt
from krausening.logging import LogManager

LogManager.get_instance().get_logger("Emitter").setLevel("DEBUG")


@given("a kafka consumer monitoring the lineage topic")
def step_impl(context):
    context.consumer = KafkaConsumer(
        "lineage-event-out", bootstrap_servers=f"localhost:{context.kafka_port}"
    )


@when("a valid kafka_emit request is issued")
def step_impl(context):
    run_obj = Run(uuid4())
    job_obj = Job("test-job")
    status = "START"
    emitter = Emitter()
    emitter.set_messaging_properties_path(
        str(os.path.join("tests/resources", "microprofile-config.properties"))
    )
    emitter.build_event_and_emit(run_obj, job_obj, status)
    context.emitter = emitter


@then("a message will be present in the kafka topic")
def step_impl(context):
    msg = context.consumer.poll(1.0)
    nt.assert_is_not_none(msg)


@given("a run event")
def step_impl(context):
    context.run = Run(uuid4())
    context.job = Job("test job")
    context.status = "START"
    context.run_event = RunEvent(
        run=context.run, job=context.job, event_type=context.status
    )


class ConsoleTestHandler(logging.Handler):
    def __init__(self):
        super().__init__()
        self.records_list = []

    def emit(self, record):
        self.records_list.append(record)


@when("the event is emitted via console")
def step_impl(context):
    emitter = Emitter()
    handler = ConsoleTestHandler()
    transport.logger.addHandler(handler)
    emitter.set_messaging_properties_path(
        str(os.path.join("tests/resources", "microprofile-config.properties"))
    )
    emitter.build_event_and_emit(context.run, context.job, context.status)
    context.emitter = emitter
    context.handler = handler


@then("the appropriate logrecord will be created")
def step_impl(context):
    nt.assert_greater_equal(len(context.handler.records_list), 1)


@given("a custom messaging configuration")
def step_impl(context):
    context.properties_file = os.path.join(
        "tests/resources", "microprofile-config-custom.properties"
    )


@when("set emitter with the custom messaging configuration")
def step_impl(context):
    emitter = Emitter()
    emitter.set_messaging_properties_path(str(context.properties_file))
    emitter.build_message_client()
    context.emitter = emitter


@then("the configuration properties have been updated")
def step_impl(context):
    nt.ok_(
        os.environ["mp.messaging.outgoing.lineage-event-out.topic"]
        == "lineage-event-out-custom"
    )
    nt.ok_(
        os.environ["mp.messaging.outgoing.lineage-event-out.connector"]
        == "smallrye-in-memory"
    )
