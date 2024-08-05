## aiSSEMBLE&trade; Extensions Data Encryption Vault Python

[![PyPI](https://img.shields.io/pypi/v/aissemble-extensions-encryption-vault-python?logo=python&logoColor=gold)](https://pypi.org/project/aissemble-extensions-encryption-vault-python/)
![PyPI - Python Version](https://img.shields.io/pypi/pyversions/aissemble-extensions-encryption-vault-python?logo=python&logoColor=gold)
![PyPI - Wheel](https://img.shields.io/pypi/wheel/aissemble-extensions-encryption-vault-python?logo=python&logoColor=gold)

This module provides a package for encrypting Python based pipeline data.  There are multiple encryption algorithms
available.  Each with their own strengths and weaknesses as outlined below.  

| Strategy                      | Description                                                                                                                                                                                                                                                                                                                                   |
|-------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| VaultRemoteEncryptionStrategy | Leverages the Hashicorp Vault secrets as a service capabilities.  This is a highly recommended strategy given it follows best practices and has the advantage of a large developer base working to secure the service.                                                                                                                        |
| VaultLocalEncryptionStrategy  | Leverages the Vault service to provide encryption keys (key rotation and secure storage) but allows for local encryption.  This is a good option if you have to encrypt large data objects.  It can also provide a performance boost over remote Vault encryption given there is no need for a roundtrip to the server for each data element. |
| AesCbcEncryptionStrategy      | A good basic 128 bit encryption strategy.  To use this you only need to supply a single encryption key in the encrypt.properties file (128 bit or 16 character).  This algorithm works well, but is less efficient than the AES GCM algorithm.                                                                                                |
| AesGcm96EncryptionStrategy    | This is a good strategy for most encryption needs.  It is efficient and strong against most attacks.  You can optionally use an encryption key retrieved from the Vault service with this strategy.                                                                                                                                           |


The following example illustrates how to perform encryption.

1. Example usage
    - Add the following to your code
    #### VaultRemoteEncryptionStrategy
    ```         
    # Uses remote Vault encryption
    from aissemble_encrypt.vault_remote_encryption_strategy import VaultRemoteEncryptionStrategy

    vault_remote = VaultRemoteEncryptionStrategy()

    # encrypt plain text data using Vault
    encrypted_value = vault_remote.encrypt('SOME PLAIN TEXT')

    # decrypt cipher text data using Vault
    decrypted_value = vault_remote.decrypt(encrypted_value)
    ```         
 
    _NOTE: If you are encrypting your data through a User Defined Function (udf) in PySpark you need to use
           the VaultLocalEncryptionStrategy (see below).  Currently the remote version causes threading issues.  This issue will
           likely be resolved in a future update to the Hashicorp Vault client_
    #### VaultLocalEncryptionStrategy
    ```
    # Uses an encryption key retrieved from the Vault server, but performs the encryption locally.
    from aissemble_encrypt.vault_local_encryption_strategy import VaultLocalEncryptionStrategy
    
    vault_local = VaultLocalEncryptionStrategy()

    # encrypt plain text data using local Vault
    encrypted_value = vault_local.encrypt('SOME PLAIN TEXT')

    # decrypt cipher text data using local Vault
    decrypted_value = vault_local.decrypt(encrypted_value)
    ```

    #### AesCbcEncryptionStrategy
    ```         
    # Uses the AES CBC encryption
    from aissemble_encrypt.aes_cbc_encryption_strategy import AesCbcEncryptionStrategy

    aes_cbc = AesCbcEncryptionStrategy()

    # encrypt plain text data using AES CBC
    encrypted_value = aes_cbc.encrypt('SOME PLAIN TEXT')

    # decrypt cipher text data using AES CBC
    decrypted_value = aes_cbc.decrypt(encrypted_value)
    ```   

    #### AesGcm96EncryptionStrategy
    ```         
    # AES GCM encryption with a 96 bit initialization vector (same algorithm as Vault)
    from aissemble_encrypt.aes_gcm_96_encryption_strategy import AesGcm96EncryptionStrategy

    aes_gcm_96 = AesGcm96EncryptionStrategy()

    # encrypt plain text data using AES GCM
    encrypted_value = aes_gcm_96.encrypt('SOME PLAIN TEXT')

    # decrypt cipher text data using AES CBC
    decrypted_value = aes_gcm_96.decrypt(encrypted_value)
    ```
## AISSEMBLE Data Encryption

This package includes one security client for calling the "Secrets as a Service" encryption service.

### Vault encryption
See the extensions-encryption [README](../../extensions-encryption/README.md#vault-encryption) for more information on how to configure Vault encryption.
