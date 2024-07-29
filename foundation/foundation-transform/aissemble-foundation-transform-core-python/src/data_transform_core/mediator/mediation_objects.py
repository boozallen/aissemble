# #%L
# Data Transform::Python::Core
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
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
