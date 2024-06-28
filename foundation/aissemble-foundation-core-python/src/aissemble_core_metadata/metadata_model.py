###
# #%L
# aiSSEMBLE Foundation::aiSSEMBLE Core (Python)
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
# #L%
###
from pydantic.main import BaseModel
from datetime import datetime
from typing import Dict
from uuid import uuid4


class MetadataModel(BaseModel):
    """
    Class that represents a common metadata model.

    resource
        the identifier of the data
    subject
        the thing acting on the data
    action
        the action being taken
    timestamp
        the time representing when the action occurred
    additionalValues
        additional values to be included in key-value pairs. Using camel-case notation to align with java implementations
        that will write to the same table
    """

    resource: str = uuid4().hex
    subject: str = ""
    action: str = ""
    timestamp: datetime = datetime.now().timestamp()
    additionalValues: Dict[str, str] = dict()
