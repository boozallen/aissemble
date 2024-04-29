# #%L
# Data Transform::Python::Core
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
# #L%
###
from data_transform_core.mediator import Mediator
from typing import Dict


class PassThroughMediator(Mediator):
    """
    A simple mediator that simply passes values through. This can be useful if
    you want to stub in a mediator, but don't have a real routine now (or
    potentially ever). It also serves at the default mediator should match not
    be found for a desired mediator lookup.
    """

    def performMediation(self, input: any, properties: Dict[str, str]) -> any:
        return input
