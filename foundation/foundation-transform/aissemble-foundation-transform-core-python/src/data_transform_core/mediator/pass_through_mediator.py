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
from typing import Dict


class PassThroughMediator(Mediator):
    """
    A simple mediator that simply passes values through. This can be useful if
    you want to stub in a mediator, but don't have a real routine now (or
    potentially ever). It also serves at the default mediator should match not
    be found for a desired mediator lookup.
    """

    def performMediation(self, input: any, properties: Dict[str, str]) -> any:
        return input
