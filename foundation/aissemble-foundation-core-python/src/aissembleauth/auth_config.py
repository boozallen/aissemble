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

import os
from krausening.properties import PropertyManager


class AuthConfig:
    """
    Configurations for authentication and authorization.
    """

    def __init__(self):
        property_manager = PropertyManager.get_instance()
        self.properties = property_manager.get_properties("auth.properties")

    def public_key_path(self):
        """
        Returns the path of the public key
        """
        return os.path.expandvars(self.properties["public_key_path"])

    def jks_path(self):
        """
        Returns the path of the keystore
        """
        return os.path.expandvars(self.properties["jks_path"])

    def jks_password(self):
        """
        Returns the keystore password
        """
        return os.path.expandvars(self.properties["jks_password"])

    def jks_key_alias(self):
        """
        Returns the key alias in the keystore.  This is used to pull the correct key.
        """
        return os.path.expandvars(self.properties["key_alias"])

    def pdp_host_url(self):
        """
        Returns the host url for the policy decision point server.
        """
        return os.path.expandvars(self.properties["pdp_host_url"])

    def is_authorization_enabled(self):
        """
        Returns whether or not authorization is enabled.
        """
        is_enabled = True
        try:
            is_authorization_enabled = self.properties["is_authorization_enabled"]
            if isinstance(is_authorization_enabled, str):
                # Properties are usually strings so we need to convert to boolean
                is_enabled = is_authorization_enabled.lower() == "true"
            elif isinstance(is_authorization_enabled, bool):
                is_enabled = is_authorization_enabled
        except Exception:
            print("Key not found: is_authorization_enabled.  Defaulting to True.")

        return is_enabled
