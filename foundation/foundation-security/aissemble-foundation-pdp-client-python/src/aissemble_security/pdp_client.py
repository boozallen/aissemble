###
# #%L
# aiSSEMBLE Foundation::aiSSEMBLE Core Security::aiSSEMBLE Security Client (Python)
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
# #L%
###
import json
import requests


class AissembleSecurityException(Exception):
    pass


class PDPClient:
    """
    REST client for calling the Policy Decision Point
    """

    def __init__(self, url):
        self.url = url

    def authorize(self, token: str, resource: str, action: str):
        """
        Makes a call to the Policy Decision Point which will return an ALLOW or DENY decision.
        """
        if not token:
            raise AissembleSecurityException("Token is required in order to authorize")

        authorize_request = {"jwt": token, "resource": resource, "action": action}

        response = requests.post(self.url, json=authorize_request)
        decision = response.text

        return decision
