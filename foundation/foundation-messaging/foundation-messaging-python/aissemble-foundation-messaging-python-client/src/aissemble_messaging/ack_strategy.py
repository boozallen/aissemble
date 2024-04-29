###
# #%L
# aiSSEMBLE::Foundation::Messaging Python
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
# #L%
###
from enum import Enum


class AckStrategy(Enum):
    """
    Represents an acknowledgement strategy for the service should respond to messages.
    """

    POSTPROCESSING = 0
    MANUAL = 1
