###
# #%L
# aiSSEMBLE::Foundation::Messaging Python
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
# #L%
###
from aissemble_messaging.exception.base_messaging_error import BaseMessagingError


class ProcessMessageError(BaseMessagingError):
    """
    An error thrown when the caller failed to process the message
    """

    def __init__(self, error_message: str):
        self.message = error_message
