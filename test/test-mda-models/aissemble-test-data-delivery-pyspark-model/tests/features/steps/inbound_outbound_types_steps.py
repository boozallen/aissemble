###
# #%L
# AIOps Foundation::AIOps MDA Patterns::Pyspark
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
# #L%
###
from behave import given, when, then  # pylint: disable=no-name-in-module
import nose.tools as nt
from krausening.logging import LogManager
from aissemble_test_data_delivery_pyspark_model.step.void_inbound_and_outbound import (
    VoidInboundAndOutbound,
)
from aissemble_test_data_delivery_pyspark_model.step.void_inbound_and_native_outbound import (
    VoidInboundAndNativeOutbound,
)
from aissemble_test_data_delivery_pyspark_model.step.void_inbound_and_messaging_outbound import (
    VoidInboundAndMessagingOutbound,
)
from aissemble_test_data_delivery_pyspark_model.step.native_inbound_and_void_outbound import (
    NativeInboundAndVoidOutbound,
)
from aissemble_test_data_delivery_pyspark_model.step.native_inbound_and_outbound import (
    NativeInboundAndOutbound,
)
from aissemble_test_data_delivery_pyspark_model.step.native_inbound_and_messaging_outbound import (
    NativeInboundAndMessagingOutbound,
)
from aissemble_test_data_delivery_pyspark_model.step.messaging_inbound_and_void_outbound import (
    MessagingInboundAndVoidOutbound,
)
from aissemble_test_data_delivery_pyspark_model.step.messaging_inbound_and_outbound import (
    MessagingInboundAndOutbound,
)
from aissemble_test_data_delivery_pyspark_model.step.void_inbound_and_outbound_async import (
    VoidInboundAndOutboundAsync,
)
from aissemble_test_data_delivery_pyspark_model.step.void_inbound_and_native_outbound_async import (
    VoidInboundAndNativeOutboundAsync,
)
from aissemble_test_data_delivery_pyspark_model.step.void_inbound_and_messaging_outbound_async import (
    VoidInboundAndMessagingOutboundAsync,
)
from aissemble_test_data_delivery_pyspark_model.step.native_inbound_and_void_outbound_async import (
    NativeInboundAndVoidOutboundAsync,
)
from aissemble_test_data_delivery_pyspark_model.step.native_inbound_and_outbound_async import (
    NativeInboundAndOutboundAsync,
)
from aissemble_test_data_delivery_pyspark_model.step.native_inbound_and_messaging_outbound_async import (
    NativeInboundAndMessagingOutboundAsync,
)
from aissemble_test_data_delivery_pyspark_model.step.messaging_inbound_and_void_outbound_async import (
    MessagingInboundAndVoidOutboundAsync,
)
from aissemble_test_data_delivery_pyspark_model.step.messaging_inbound_and_outbound_async import (
    MessagingInboundAndOutboundAsync,
)
import inspect
import re
from pathlib import Path

logger = LogManager.get_instance().get_logger(Path(__file__).name)


@when("a synchronous step with void inbound type and void outbound type is defined")
def step_impl(context):
    context.step = VoidInboundAndOutbound()


@when("a synchronous step with void inbound type and native outbound type is defined")
def step_impl(context):
    context.step = VoidInboundAndNativeOutbound()


@when(
    "a synchronous step with void inbound type and messaging outbound type is defined"
)
def step_impl(context):
    context.step = VoidInboundAndMessagingOutbound()


@when("a synchronous step with native inbound type and void outbound type is defined")
def step_impl(context):
    context.step = NativeInboundAndVoidOutbound()


@when("a synchronous step with native inbound type and native outbound type is defined")
def step_impl(context):
    context.step = NativeInboundAndOutbound()


@when(
    "a synchronous step with native inbound type and messaging outbound type is defined"
)
def step_impl(context):
    context.step = NativeInboundAndMessagingOutbound()


@when(
    "a synchronous step with messaging inbound type and void outbound type is defined"
)
def step_impl(context):
    context.step = MessagingInboundAndVoidOutbound()


@when(
    "a synchronous step with messaging inbound type and messaging outbound type is defined"
)
def step_impl(context):
    context.step = MessagingInboundAndOutbound()


@when("an asynchronous step with void inbound type and void outbound type is defined")
def step_impl(context):
    context.step = VoidInboundAndOutboundAsync()


@when("an asynchronous step with void inbound type and native outbound type is defined")
def step_impl(context):
    context.step = VoidInboundAndNativeOutboundAsync()


@when(
    "an asynchronous step with void inbound type and messaging outbound type is defined"
)
def step_impl(context):
    context.step = VoidInboundAndMessagingOutboundAsync()


@when("an asynchronous step with native inbound type and void outbound type is defined")
def step_impl(context):
    context.step = NativeInboundAndVoidOutboundAsync()


@when(
    "an asynchronous step with native inbound type and native outbound type is defined"
)
def step_impl(context):
    context.step = NativeInboundAndOutboundAsync()


@when(
    "an asynchronous step with native inbound type and messaging outbound type is defined"
)
def step_impl(context):
    context.step = NativeInboundAndMessagingOutboundAsync()


@when(
    "an asynchronous step with messaging inbound type and void outbound type is defined"
)
def step_impl(context):
    context.step = MessagingInboundAndVoidOutboundAsync()


@when(
    "an asynchronous step with messaging inbound type and messaging outbound type is defined"
)
def step_impl(context):
    context.step = MessagingInboundAndOutboundAsync()


@then("the step execution has no inbound parameter")
def step_impl(context):
    validateInboundParameter(context.step.execute_step, None)
    validateInboundParameter(context.step.execute_step_impl, None)


@then("the step execution has no outbound return type")
def step_impl(context):
    validateOutboundReturnType(context.step.execute_step, None)
    validateOutboundReturnType(context.step.execute_step_impl, None)


@then('the step execution has an inbound parameter of type "DataFrame"')
def step_impl(context):
    validateInboundParameter(context.step.execute_step, "DataFrame")
    validateInboundParameter(context.step.execute_step_impl, "DataFrame")


@then('the step execution has an outbound return type of "DataFrame"')
def step_impl(context):
    validateOutboundReturnType(context.step.execute_step, "DataFrame")
    validateOutboundReturnType(context.step.execute_step_impl, "DataFrame")


@then(
    "the step execution base has no inbound parameter because it consumes the inbound from kafka"
)
def step_impl(context):
    validateInboundParameter(context.step.execute_step, None)
    nt.ok_(
        hasMethod(context.step, "consume_from_kafka"),
        "Step does not contain expected method to consume from kafka",
    )
    source = inspect.getsource(context.step.execute_step)
    nt.ok_(
        "self.consume_from_kafka()" in source,
        f"Step execution base does not consume from kafka: {source}",
    )


@then(
    'the step execution implementation has an inbound parameter of "str" to handle the inbound from kafka'
)
def step_impl(context):
    validateInboundParameter(context.step.execute_step_impl, "str")


@then(
    "the step execution base has no outbound return type because it sends the outbound to kafka"
)
def step_impl(context):
    validateOutboundReturnType(context.step.execute_step, None)
    if hasMethod(context.step, "consume_from_kafka"):
        logger.info(
            f"Checking {type(context.step).__name__} consume_from_kafka method for send logic"
        )
        source = inspect.getsource(context.step.consume_from_kafka)
    else:
        logger.info(
            f"Checking {type(context.step).__name__} execute_step method for send logic"
        )
        source = inspect.getsource(context.step.execute_step)
    sendpattern = re.compile(
        r'self.producer.send\(["\']outboundChannel["\'], value=outbound.encode\(\)\)'
    )
    nt.ok_(
        sendpattern.search(source) is not None,
        f"Step execution base does not send to kafka: {source}",
    )


@then(
    'the step execution implementation has an outbound return type of "str" to send to kafka'
)
def step_impl(context):
    validateOutboundReturnType(context.step.execute_step_impl, "str")


@then("the step execution is async")
def step_impl(context):
    async_base = inspect.iscoroutinefunction(context.step.execute_step)
    nt.ok_(async_base, "Step execution base is not async")

    async_impl = inspect.iscoroutinefunction(context.step.execute_step_impl)
    nt.ok_(async_impl, "Step execution impl is not async")


def validateInboundParameter(method, expectedType):
    methodSignature = inspect.signature(method)
    if expectedType is None:
        nt.ok_(
            len(methodSignature.parameters) == 0,
            "Method signature has inbound parameter when it should not",
        )
    else:
        nt.ok_(
            len(methodSignature.parameters) == 1,
            "Method signature has unexpected number of inbound parameters",
        )
        inboundParameter = methodSignature.parameters["inbound"]
        inboundParameterType = inboundParameter.annotation
        nt.ok_(
            inboundParameterType.__name__ == expectedType,
            "Unexpected inbound parameter type found",
        )


def validateOutboundReturnType(method, expectedType):
    methodSignature = inspect.signature(method)
    if expectedType is None:
        nt.ok_(
            methodSignature.return_annotation is None,
            "Method signature has outbound return type when it should not",
        )
    else:
        outboundType = methodSignature.return_annotation
        nt.ok_(
            outboundType.__name__ == expectedType,
            "Unexpected outbound return type found",
        )


def hasMethod(step, methodName):
    return hasattr(step, methodName) and inspect.ismethod(getattr(step, methodName))
