###
# #%L
# aiSSEMBLE::Foundation::Messaging Python
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
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
