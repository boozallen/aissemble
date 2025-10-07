###
# #%L
# aiSSEMBLE Foundation::aiSSEMBLE Core (Python)
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
