# #%L
# Data Transform::Python::Core
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
# #L%
###
from behave import given, when, then  # pylint: disable=no-name-in-module
import nose.tools as nt
from krausening.logging import LogManager
from data_transform_core.mediator import MediationManager
from data_transform_core.mediator import MediationConfiguration
from data_transform_core.mediator import MediationContext
from data_transform_core.mediator import MediationProperty
from data_transform_core.mediator import Mediator
from data_transform_core.mediator import PassThroughMediator
from data_transform_core.mediator import MediationException


LogManager.get_instance().get_logger("LoggingMediator").setLevel("DEBUG")


@given("the following mediation configurations")
def step_impl(context):
    context.mediationConfigurations = []
    for row in context.table:
        inputType = row["inputType"]
        outputType = row["outputType"]
        className = row["className"]
        context.mediationConfigurations.append(
            MediationConfiguration(
                inputType=inputType, outputType=outputType, className=className
            )
        )


@given('a mediator with "{inputType}", "{outputType}", and "{className}"')
def step_impl(context, inputType, outputType, className):
    context.mediationConfiguration = MediationConfiguration(
        inputType=inputType, outputType=outputType, className=className
    )


@given("the following properties")
def step_impl(context):
    properties = []
    for row in context.table:
        key = row["key"]
        value = row["value"]
        properties.append(MediationProperty(key=key, value=value))

    context.mediationConfiguration.properties = properties
    context.mediationConfigurations.append(context.mediationConfiguration)


@when("mediation is configured for runtime")
def step_impl(context):
    context.mediationManager = MediationManager()
    for mediationConfiguration in context.mediationConfigurations:
        context.mediationManager.validateAndAddMediator(
            context.mediationConfigurations, mediationConfiguration
        )


@when(
    'the mediator is invoked for input "{inputType}" and output "{outputType}" values "{inputValue}"'
)
def step_impl(context, inputType, outputType, inputValue):
    invokeMediator(context, inputType, outputType, inputValue)


@then(
    'a valid mediation routine is available for the intersection of "{inputType}" and "{outputType}"'
)
def step_impl(context, inputType, outputType):
    findMediator(context.mediationManager, inputType, outputType)


@then(
    'a valid mediation routine is NOT available for the intersection of "{inputType}" and "{outputType}"'
)
def step_impl(context, inputType, outputType):
    mediationContext = MediationContext(inputType=inputType, outputType=outputType)
    mediator = context.mediationManager.getMediator(mediationContext)
    nt.ok_(
        isinstance(mediator, PassThroughMediator),
        "The default pass through mediator should have been returned!",
    )


@then('the input is transformed to "{outputValue}"')
def step_impl(context, outputValue):
    nt.ok_(
        context.outputValue == outputValue, "Output value was not what was anticipated!"
    )
    nt.ok_(
        context.encounteredException is None,
        "Should not have encountered a MediationException!",
    )


@then("a graceful exception case is returned")
def step_impl(context):
    nt.ok_(context.outputValue is None, "Should not have returned an output!")
    nt.ok_(
        context.encounteredException is not None,
        "Should have encountered MediationException!",
    )


def findMediator(
    mediationManager: MediationManager, inputType: str, outputType: str
) -> Mediator:
    mediationContext = MediationContext(inputType=inputType, outputType=outputType)
    mediator = mediationManager.getMediator(mediationContext)
    nt.ok_(
        mediator is not None,
        "Could not access mediator for context %s" % mediationContext,
    )
    return mediator


def invokeMediator(context, inputType: str, outputType: str, inputValue: any) -> None:
    context.outputValue = None
    context.encounteredException = None
    mediator = findMediator(context.mediationManager, inputType, outputType)
    try:
        context.outputValue = mediator.mediate(inputValue)
    except MediationException as e:
        context.encounteredException = e
