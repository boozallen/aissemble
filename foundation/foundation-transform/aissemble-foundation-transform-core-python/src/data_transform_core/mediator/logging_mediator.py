# #%L
# Data Transform::Python::Core
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
from data_transform_core.mediator import Mediator
from krausening.logging import LogManager
from typing import Dict


class LoggingMediator(Mediator):
    """
    A pass-through mediator that logs output at a debug level.
    """

    __logger = LogManager.get_instance().get_logger("LoggingMediator")

    def performMediation(self, input: any, properties: Dict[str, str]) -> any:
        if input is not None:
            LoggingMediator.__logger.debug(input)
        else:
            LoggingMediator.__logger.debug("No data to mediate!")

        return input
