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


class TopicNotSupportedError(BaseMessagingError):
    """
    An error thrown when the caller has attempted to subscribe to a topic that cannot be found in the service.
    """

    topic: str

    def __init__(self, topic: str):
        self.topic = topic
        self.message = "Could not find a topic to subscribe to named " + topic
