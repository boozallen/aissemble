###
# #%L
# aiSSEMBLE::Foundation::Messaging Python
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
# #L%
###
import uuid
from typing import Any


class Future:
    """
    Used to track message promises and produce Acks.
    """

    serviceFuture: Any
    futureId: uuid

    def __init__(self, obj):
        self.serviceFuture = obj
        self.futureId = uuid.uuid4()

    def getResult(self) -> Any:
        """
        Produces an acknowledgement for the message that this is a promise for.
        """
        print("not implemented")
