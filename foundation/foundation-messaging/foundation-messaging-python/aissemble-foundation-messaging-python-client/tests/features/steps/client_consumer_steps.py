from behave import given, when, then  # pylint: disable=no-name-in-module
import aissemble_messaging
from aissemble_messaging import *
from unittest.mock import patch
import nose.tools as nt

# Mock a service with a subscribe endpoint that returns a subscription if
# the topic name exists, otherwise throws an exception


@given('a messaging topic named "{topic_name}"')
def step_impl(context, topic_name):
    context.topics.append(topic_name)


@when('I subscribe to the topic "{topic_name}"')
def step_impl(context, topic_name):
    with patch("py4j.java_gateway.JVMView") as mock_jvm:
        with patch("py4j.java_gateway.JavaObject") as mock_service:
            if topic_name in (context.topics):
                context.error = None
            else:
                mock_service.subscribe.side_effect = (
                    aissemble_messaging.TopicNotSupportedError(topic_name)
                )
            try:
                context.client = MessagingClient()
                context.client.service_handle = mock_service
                context.client.subscribe(topic_name, None, AckStrategy.POSTPROCESSING)
            except TopicNotSupportedError as error:
                context.error = error


@then("no exceptions are thrown")
def step_impl(context):
    nt.assert_is_none(context.error)


@then("I receive an exception telling me that it doesn't exist")
def step_impl(context):
    nt.ok_(context.error.message)


@then("the exception is a TopicNotSupportedError exception")
def step_impl(context):
    nt.ok_("TopicNotSupportedError" == type(context.error).__name__)
