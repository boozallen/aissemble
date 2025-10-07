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
"""
Configurations for authentication and authorization, read from the auth properties file.
"""

from krausening.properties import PropertyManager


class InferenceConfig:
    """
    Configurations for inference
    """

    def __init__(self):
        property_manager = PropertyManager.get_instance()
        self.properties = property_manager.get_properties("inference.properties")

    def rest_service_url(self):
        """
        Returns URL of inference REST service.
        """
        return self.properties.getProperty("rest_service_url", "http://localhost")

    def rest_service_port(self):
        """
        Returns port of inference REST service.
        """
        return self.properties.getProperty("rest_service_port", "7080")

    def grpc_service_url(self):
        """
        Returns the URL of the inference gRPC service.
        """
        return self.properties.getProperty("grpc_service_url", "http://localhost")

    def grpc_service_port(self):
        """
        Returns the port of the inference gRPC service.
        """
        return self.properties.getProperty("grpc_service_port", "7081")
