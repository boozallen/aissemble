# Security

The following tasks will help you add authentication to your module.

1. Generate a jks key store with at least one public/private key pair.
    - Here is an example:
        - ```openssl pkcs12 -export -in cacert.pem -inkey cakey.pem -out identity.p12 -name "aiopskey"```
        -  ```keytool -importkeystore -destkeystore aiops-secure.jks -deststorepass password -srckeystore identity.p12 -srcstoretype PKCS12 -srcstorepass password```
        -  ```keytool -import -file cacert.pem -keystore aiops-secure.jks -storepass password```
1. Add the jks file to your environment and set the following krausening property to point to your keystore location 
    - ```keystore.file.location=/path/to/my/keystore.jks```
1. Use one of the authentication methods from com.boozallen.aiops.cookbook.authorization or create your own class that 
imlements the AiopsSecureTokenServiceClient interface 
(_currently we have one for Keycloak and one for simple JWT_) 
    - Add the following to your code
    - 
    ```         
      AiopsSecureTokenServiceClient aiopsSecureTokenServiceClient = new AiopsSimpleSecureTokenServiceClient();
      aiopsSecureTokenServiceClient.authenticate("aiops", "password");
      String token = aiopsSecureTokenServiceClient.getJWTToken();
    ```
    - You can then use the information contained in the JWT token to allow/deny access 
    
    
### Vault encryption
See the extensions-encryption [README](../../extensions-encryption/README.md#vault-encryption) for more information on how to configure Vault encryption.