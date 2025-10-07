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
from pydantic import BaseModel
from typing import List, Optional


class MediationException(Exception):
    """
    All exceptions that are encountered during the execution of a mediator will be wrapped in this exception.
    """

    pass


class MediationContext(BaseModel):
    """
    Provides contextual information about a mediation that is to be performed.
    """

    inputType: str
    outputType: str

    def __hash__(self) -> int:
        return hash((self.inputType, self.outputType))


class MediationProperty(BaseModel):
    """
    Allows key value pairs to be added to mediator configurations. These are
    especially useful adding additional information, such as configuration option
    information, into a specific mediator.
    """

    key: str
    value: str


class MediationConfiguration(MediationContext):
    """
    Contains MediationContext information in additional to the class name of
    the mediator that will provide the implementation to mediate from input
    type to output type.
    """

    className: str
    properties: Optional[List[MediationProperty]] = None

    def getShortClassName(self) -> str:
        """
        Returns the short class name for the mediator class.
        """
        split = self.className.rsplit(".", 1)
        return split[0] if len(split) == 1 else split[1]

    def getPackageName(self) -> str:
        """
        Returns the package name for the mediator class.
        """
        split = self.className.rsplit(".", 1)
        return "" if len(split) == 1 else split[0]
