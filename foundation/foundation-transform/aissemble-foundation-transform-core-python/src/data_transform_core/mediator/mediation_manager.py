# #%L
# Data Transform::Python::Core
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
# #L%
###
from typing import Any, Dict, List
from importlib import import_module
from krausening.logging import LogManager
from data_transform_core.mediator import (
    MediationContext,
    MediationConfiguration,
    Mediator,
    PassThroughMediator,
    MediationException,
)


class MediationManager:
    """
    Contains all registered mediation options, performing lookups to find the
    appropriate mediation option for a given input and output type combination.
    """

    __logger = LogManager.get_instance().get_logger("MediationManager")
    __defaultPassThroughMediator = PassThroughMediator()

    def __init__(self) -> None:
        self._mediationOptionMap: Dict[MediationContext, Any] = dict()
        self._mediationPropertyMap: Dict[MediationContext, Dict[str, str]] = dict()

    def validateAndAddMediator(
        self,
        mediationConfigurations: List[MediationConfiguration],
        mediationConfiguration: MediationConfiguration,
    ) -> None:
        """
        Validates and adds a mediator to this mediation manager.
        """
        priorInstance = None
        context = None
        mediator = None
        try:
            # Dynamically load python class
            packageName = mediationConfiguration.getPackageName()
            className = mediationConfiguration.getShortClassName()
            mediator = getattr(import_module(packageName), className)
            if not issubclass(mediator, Mediator):
                MediationManager.__logger.warn(
                    "The specified class %s is not a valid mediator!"
                    % mediationConfiguration.className
                )
                mediator = None

            # Create mediation context
            context = MediationContext(
                inputType=mediationConfiguration.inputType,
                outputType=mediationConfiguration.outputType,
            )
            priorInstance = self._mediationOptionMap.get(context)
            if priorInstance:
                MediationManager.__logger.warn(
                    "Duplicate mediation definitions specified for %s"
                    % mediationConfigurations
                )

            # Add mediator and its properties
            self._mediationOptionMap[context] = mediator
            self.addMediatorProperties(mediationConfiguration, context)

        except Exception:
            MediationManager.__logger.warn(
                "The specified class %s was not found!"
                % mediationConfiguration.className
            )

    def addMediatorProperties(
        self, mediationConfiguration: MediationConfiguration, context: MediationContext
    ) -> None:
        """
        Adds the properties for the mediator to this mediator manager.
        """
        if mediationConfiguration.properties:
            mediatorProperties = dict()
            for property in mediationConfiguration.properties:
                mediatorProperties[property.key] = property.value

            self._mediationPropertyMap[context] = mediatorProperties

    def getMediator(self, context: MediationContext) -> Mediator:
        """
        Returns an instance of the mediator that is mapped to the input type and
        output type within the passed MediationContext. This will return a
        pass-through (i.e., no-op) mediator if a match is not found so that users
        do not have to worry about checking for a valid mediator - they can just
        invoke it without worry.
        """
        clazz = self._mediationOptionMap.get(context)
        mediator = None

        if clazz:
            try:
                mediator = clazz()
                mediator.properties = self._mediationPropertyMap.get(context)
            except Exception:
                raise MediationException("Could not create class %s" % clazz)

        if not mediator:
            MediationManager.__logger.warn(
                "Could not find mediator for %s:%s - using PassThroughMediator instead!"
                % (context.inputType, context.outputType)
            )
            mediator = MediationManager.__defaultPassThroughMediator

        return mediator
