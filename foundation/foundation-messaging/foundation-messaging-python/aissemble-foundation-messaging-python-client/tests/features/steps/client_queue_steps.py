from behave import given, when, then  # pylint: disable=no-name-in-module
from aissemble_messaging import MessagingClient, Message, TopicNotSupportedError
import nose.tools as nt
from py4j.java_gateway import launch_gateway
import os


@given('a messaging topic named "{topic_name}" exists in the service')
def step_impl(context, topic_name):
    os.environ["mp.messaging.outgoing.channel-a-out.connector"] = "smallrye-in-memory"
    os.environ["mp.messaging.outgoing.channel-a-out.value.serializer"] = (
        "org.apache.kafka.common.serialization.StringSerializer"
    )
    os.environ["mp.messaging.outgoing.channel-a-out.topic"] = topic_name
    context.service_port = launch_gateway(
        classpath="src/aissemble_messaging/service_resources/classpath/*",
        die_on_exit=True,
        redirect_stdout=1,
    )


@given('a messaging topic named "TopicD" does not exist in the service')
def step_impl(context):
    context.service_port = launch_gateway(
        classpath="src/aissemble_messaging/service_resources/classpath/*",
        die_on_exit=True,
        redirect_stdout=1,
    )


@when('a message is queued to "{topic_name}"')
def step_impl(context, topic_name):
    try:
        context.client = MessagingClient(context.service_port)
        message = Message()
        message.set_payload("test message")
        context.result = context.client.queueForSend(topic_name, message)
    except TopicNotSupportedError as error:
        context.result = None
        context.error = error


@then("I receive a Future object for my queued message")
def step_impl(context):
    nt.ok_(context.result)
    nt.ok_("Future" == type(context.result).__name__)


@then("I receive a NoTopicSupportedError instead of a Future")
def step_impl(context):
    nt.ok_(context.error.message)
    nt.ok_("TopicNotSupportedError" == type(context.error).__name__)
