# #%L
# Data Transform::Python::Core
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
# #L%
###
from abc import ABC, abstractmethod
from typing import Dict
from data_transform_core.mediator import MediationException


class Mediator(ABC):
    """
    Defines the contract for a class that provides mediation capabilities.
    """

    def __init__(self) -> None:
        self._properties: Dict[str, str] = None

    def mediate(self, input: any) -> any:
        """
        Performs mediation based on the input and associated mediation context.
        """
        result = None
        try:
            # Python doesn't support method overloading, so no need to try to find
            # the method based on the type of the input parameter like mash does
            result = self.performMediation(input, self._properties)
        except Exception:
            raise MediationException("Issue encountered while performing mediation")

        return result

    @property
    def properties(self) -> Dict[str, str]:
        return self._properties

    @properties.setter
    def properties(self, properties: Dict[str, str]) -> None:
        self._properties = properties

    @abstractmethod
    def performMediation(self, input: any, properties: Dict[str, str]) -> any:
        pass
