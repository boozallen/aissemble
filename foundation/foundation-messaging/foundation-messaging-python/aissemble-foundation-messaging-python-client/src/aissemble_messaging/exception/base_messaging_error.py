###
# #%L
# aiSSEMBLE::Foundation::Messaging Python
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
# #L%
###
from typing import Any


class BaseMessagingError(Exception):
    """
    Base class containing common fields for various messaging errors that can occur.
    """

    cause: Any
    message: str
