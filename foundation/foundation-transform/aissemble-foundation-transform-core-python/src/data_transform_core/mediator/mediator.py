# #%L
# Data Transform::Python::Core
# %%
# Copyright (C) 2021 Booz Allen
# %%
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
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
