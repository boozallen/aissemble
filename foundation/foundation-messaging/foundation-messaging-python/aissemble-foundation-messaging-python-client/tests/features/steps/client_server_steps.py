from behave import given, when, then  # pylint: disable=no-name-in-module
from aissemble_messaging import (
    MessagingClient,
    AckStrategy,
    TopicNotSupportedError,
    Callback,
    Message,
    MessageHandle,
)
from py4j.java_gateway import launch_gateway, java_import
import nose.tools as nt
import os


@given('a messaging topic "{topic_name}" that exists in the service')
def step_impl(context, topic_name):
    os.environ["mp.messaging.incoming.channel-a-in.connector"] = "smallrye-in-memory"
    os.environ["mp.messaging.incoming.channel-a-in.value.deserializer"] = (
        "org.apache.kafka.common.serialization.StringDeserializer"
    )
    os.environ["mp.messaging.incoming.channel-a-in.topic"] = topic_name

    context.service_port = launch_gateway(
        classpath="dist/dependency/*",
        die_on_exit=True,
        redirect_stdout=1,
    )


@given('a messaging topic "TopicD" that does not exist in the service')
def step_impl(context):
    print("Nothing to do here")


@when('the messaging client subscribes to "{topic_name}"')
def step_impl(context, topic_name):
    try:
        context.client = MessagingClient(context.service_port)
        context.client.subscribe(topic_name, None, AckStrategy.POSTPROCESSING)
    except TopicNotSupportedError as error:
        context.error = error


@then("I receive a NoTopicSupportedError")
def step_impl(context):
    nt.ok_(context.error.message)
    nt.ok_("TopicNotSupportedError" == type(context.error).__name__)


@then('the messaging client confirms the subscription to "{topic_name}"')
def step_impl(context, topic_name):
    context.result = context.client.isTopicSubscribed(topic_name)
    nt.assert_true(context.result)


@given('consumer subscribe to a topic named "{topic_name}"')
def step_impl(context, topic_name):
    context.service_port = launch_gateway(
        classpath="dist/dependency/*",
        die_on_exit=True,
        redirect_stdout=1,
    )
    context.topic = topic_name
    context.callback = TestCallback(process_message)
    context.client = MessagingClient(context.service_port)


@given("the subscription is configured with the {ackStrategy} strategy")
def step_impl(context, ackStrategy):
    context.client.subscribe(
        context.topic, context.callback, get_ack_strategy(ackStrategy)
    )


@when("a message is sent to the topic")
def step_impl(context):
    os.environ["payload"] = "test message"
    service_process_message(context)


@then("the message is processed successfully by the consumer")
def step_impl(context):
    nt.ok_(os.environ["payload"] == os.environ["payload_received"])


@when("the consumer {acknowledge} the message")
def step_impl(context, acknowledge):
    os.environ["payload"] = acknowledge
    service_process_message(context)


@then("the {acknowledge} is sent to the broker")
def step_impl(context, acknowledge):
    # message is received by the consumer
    nt.ok_(acknowledge == os.environ["payload_received"])
    # completeStage is sent from consumer
    nt.ok_(acknowledge == context.completionStage.get())


def process_message(message: Message):
    os.environ["payload_received"] = message.get_payload()
    if os.environ["payload_received"] == "nack":
        return message.nack("caller nacks the message")
    return message.ack()


def get_ack_strategy(ackStrategy):
    if ackStrategy == "MANUAL":
        return AckStrategy.MANUAL
    elif ackStrategy == "POSTPROCESSING":
        return AckStrategy.POSTPROCESSING


def service_process_message(context):
    java_import(
        context.client.gateway.jvm,
        "org.eclipse.microprofile.reactive.messaging.Message",
    )
    java_message = context.client.gateway.jvm.Message.of(os.environ["payload"])
    # java service process the message
    context.completionStage = context.client.getSubscription(
        context.topic
    ).processMessage(java_message)


class TestCallback(Callback):
    def execute(self, message: MessageHandle) -> object:
        # include the assert_message to the complete future object for verification
        assert_message = super()._create_message(message).get_payload()
        if assert_message != "test message":
            return (
                super()
                .execute(message)
                .toCompletableFuture()
                .completedFuture(assert_message)
            )
        else:
            return super().execute(message)
