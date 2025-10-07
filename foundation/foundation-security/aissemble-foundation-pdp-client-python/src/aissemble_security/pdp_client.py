###
# #%L
# aiSSEMBLE Foundation::aiSSEMBLE Core Security::aiSSEMBLE Security Client (Python)
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
