###
# #%L
# Policy-Based Configuration::Policy Manager (Python)
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
from krausening.properties import PropertyManager


class PolicyConfiguration:
    """
    PolicyConfiguration is used to configure the policy location and defaults.
    """

    def __init__(self) -> None:
        self.properties = PropertyManager.get_instance().get_properties(
            "policy-configuration.properties"
        )

    def policiesLocation(self) -> str:
        """
        Configures the location and file name of the file that contains the
        policies.
        """
        try:
            return self.properties["policies-location"]
        except TypeError:
            return None
