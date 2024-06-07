## aiSSEMBLE&trade; Foundation Policy Decision Point Python Client

[![PyPI](https://img.shields.io/pypi/v/aissemble-foundation-pdp-client-python?logo=python&logoColor=gold)](https://pypi.org/project/aissemble-foundation-pdp-client-python/)
![PyPI - Python Version](https://img.shields.io/pypi/pyversions/aissemble-foundation-pdp-client-python?logo=python&logoColor=gold)
![PyPI - Wheel](https://img.shields.io/pypi/wheel/aissemble-foundation-pdp-client-python?logo=python&logoColor=gold)

This module provides a client for authorizing a jwt, action and resource

The following tasks will help you add authentication to your module.

1. Example usage
    - Add the following to your code
    - 
    ```python         
        # The url points to your policy decision point base
        # For example http://localhost:8080/api/pdp
        pdp_client = PDPClient(auth_config.pdp_host_url()) 
        decision = pdp_client.authorize(your_jwt_string, None, 'some-action')
        if 'PERMIT' == decision:
            print('User is authorized to access XYZ')
        else:
            raise AissembleSecurityException('User is not authorized')
    ```
