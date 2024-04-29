# Python security client

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
            raise AiopsSecurityException('User is not authorized')
    ```
