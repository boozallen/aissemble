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

"""
Implements the MessageHandle Java interface specified by the service to allow python process the Java
microprofile message
"""


class MessageHandle:
    def __init__(self):
        pass

    def getPayload(self) -> object:
        """
        There is no implementation on python side. This allows python class to call the Java getPayload()
        function.
        """
        pass

    def ack(self) -> object:
        """
        There is no implementation on python side. This allows python class to call the Java MessageHandle ack()
        function.
        """
        pass

    def nack(self, reason: str) -> object:
        """
        There is no implementation on python side. This allows python class to call the Java MessageHandle nack()
        function
        :param reason: the reason to nack the message
        """
        pass

    class Java:
        implements = ["com.boozallen.aissemble.messaging.python.transfer.MessageHandle"]
