# #%L
# Data Transform::Python::Core
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
# #L%
###
from data_transform_core.mediator import Mediator
from krausening.logging import LogManager
from typing import Dict


class LoggingMediator(Mediator):
    """
    A pass-through mediator that logs output at a debug level.
    """

    __logger = LogManager.get_instance().get_logger("LoggingMediator")

    def performMediation(self, input: any, properties: Dict[str, str]) -> any:
        if input is not None:
            LoggingMediator.__logger.debug(input)
        else:
            LoggingMediator.__logger.debug("No data to mediate!")

        return input
