###
# #%L
# aiSSEMBLE Foundation::aiSSEMBLE Core (Python)
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
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
