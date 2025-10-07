###
# #%L
# aiSSEMBLE::Foundation::Messaging Python
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
from aissemble_messaging.transfer.message_handle import MessageHandle


class Message:
    """
    This is a wrapper class around the MessageHandle, which is the information that is emitted and received. It contains
    the functions to ack or nack message, and the function to retrieve the payload
    """

    def __init__(self, message_handle=None):
        """
        Constructor for Message object; requires either a MessageHandle object or a payload to
        create a python Message
        :param message_handle: the MessageHandle object associated with python message
        """
        self.message_handle = message_handle
        self.content = None

    def ack(self) -> object:
        """
        ack the message
        """
        return self.message_handle.ack()

    def nack(self, errorMessage: str) -> object:
        """
        nack the message
        """
        return self.message_handle.nack(errorMessage)

    def get_payload(self) -> object:
        """
        get payload of the message
        """
        if self.message_handle is None:
            return self.content

        return self.message_handle.getPayload()

    def set_payload(self, content: str):
        """
        set payload of the message
        """
        self.content = content
