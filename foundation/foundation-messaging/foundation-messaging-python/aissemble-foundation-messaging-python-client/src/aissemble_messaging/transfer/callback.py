###
# #%L
# aiSSEMBLE::Foundation::Messaging Python::Client
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
from typing import Callable

from krausening.logging import LogManager

from aissemble_messaging.message import Message
from aissemble_messaging.transfer.message_handle import MessageHandle

from aissemble_messaging.exception.process_message_error import ProcessMessageError


class Callback(object):
    """
    Implements the Callback Java interface specified by the service to allow py4j to freely pass
    the callback object between the client and service
    """

    def __init__(self, process: Callable):
        """
        Constructor for Callback object; requires caller to pass in a function defining
        how message will be processed
        :param process: function defining how message will be processed
        """
        self.process = process
        self.logger = LogManager.get_instance().get_logger("Callback")

    def execute(self, message_handle: MessageHandle) -> object:
        """
        Converts the Java message handle to a python Message then processes it with the specified callable process.
        :param message_handle: the Java MessageHandle from service
        """
        try:
            msg = self._create_message(message_handle)
            return self.process(msg)
        except Exception as e:
            # catch any error and wrap it in ProcessMessageError
            raise ProcessMessageError(repr(e))

    def _create_message(self, message_handle: MessageHandle) -> Message:
        """
        Helper function to convert the Java message handle into a python Message object
        :param message_handle: the Java MessageHandle from service
        """
        "the deserialization logic can be implemented here"

        msg = Message(message_handle)
        return msg

    class Java:
        """
        Enables py4j to know the custom Java interface the python Callback class is implementing
        """

        implements = ["com.boozallen.aissemble.messaging.python.transfer.Callback"]
