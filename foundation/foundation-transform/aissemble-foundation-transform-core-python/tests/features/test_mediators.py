# #%L
# aiSSEMBLE::Foundation::Transform::Python
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
# #L%
###
from data_transform_core.mediator import Mediator
from typing import Dict


class LowercaseMediator(Mediator):
    def performMediation(self, input: any, properties: Dict[str, str]) -> any:
        inputAsString = str(input)
        return inputAsString.lower()


class ExceptionalMediator(Mediator):
    def performMediation(self, input: any, properties: Dict[str, str]) -> any:
        raise RuntimeError("***BOOM*** You asked for this exception!")


class PropertyAwareMediator(Mediator):
    def performMediation(self, input: any, properties: Dict[str, str]) -> any:
        propertyA = properties["propertyA"]
        propertyB = properties["propertyB"]
        return propertyA + "-" + input + "-" + propertyB
