# extensions-encryption
Services used to encrypt data

### Vault encryption
In order to encrypt data the following properties need to be set in `encrypt.properties`. Note that the root and
unseal keys will change each time the Vault server is initialized.

* **secrets.host.url**=[Some Url]
  * Example: http://127.0.0.1:8217
* **secrets.unseal.keys**=[Comma delimeted set of keys]
  * Example (no quotes or whitespace): key1,key2,key3,key4,key5
* **secrets.root.key**=[The root key]

In a production system a System Administrator would start the Vault service and perform the unsealing, but for
demonstration purposes the java code will perform the unsealing with the provided keys.

To retrieve the keys you can read the following files on the Vault server (see commands below)

``` 
docker exec vault cat /root_key.txt 
docker exec vault cat /unseal_keys.txt 
docker exec vault cat /transit_client_token.txt
```

You can also check the vault console log.  The keys will be printed at the top

```
vault    | ROOT KEY
vault    | s.EXAMPLE
vault    | UNSEAL KEYS
vault    | ["key1", "key2", "key3", "key4", "key5"]
vault    | TRANSIT TOKEN
vault    | {"request_id": "29d26f42-7be2-9b06-c4ce-1ecc94114393", "lease_id": "", "renewable": false, "lease_duration": 0, "data": null, "wrap_info": null, "warnings": null, "auth": {"client_token": "s.Token", "accessor": "zFcMdiOHhtXUyRTUigkePpzS", "policies": ["app-aiops", "default"], "token_policies": ["app-aiops", "default"], "metadata": null, "lease_duration": 2764800, "renewable": true, "entity_id": "", "token_type": "service", "orphan": false}}
```

## Building the project
From the root of the project directory, run the build with the following command:

`./mvnw clean install -pl :foundation-encryption-policy-java,:aissemble-foundation-encryption-policy-python,:extensions-encryption-vault-java,:aissemble-extensions-encryption-vault-python,:aissemble-vault`

Unit tests to verify encryption using AES are executed during the build. Check for the following output to verify the unit tests:
- Java:
    ```
   [INFO] --- maven-surefire-plugin:3.0.0-M7:test (default-test) @ extensions-encryption-vault-java ---
  [INFO] Using auto detected provider org.apache.maven.surefire.junit4.JUnit4Provider
  [INFO]
  [INFO] -------------------------------------------------------
  [INFO]  T E S T S
  [INFO] -------------------------------------------------------
  [INFO] Running com.boozallen.aissemble.data.encryption.CucumberTest
  09:33:22.236 [main] WARN  org.technologybrewery.krausening.Krausening.logFileDoesNotExist():195 - KRAUSENING_EXTENSIONS refers to a location that does not exist: /Users/ryan/dev/aissemble/extensions/extensions-encryption/extensions-encryption-vault-java/src/test/resources/krausening/test
  09:33:22.249 [main] INFO  com.boozallen.aissemble.data.encryption.HashingSteps.a_data_element_is_provided():44 - data element provided
  09:33:22.259 [main] INFO  com.boozallen.aissemble.data.encryption.HashingSteps.a_hash_is_generated():67 - data element hashed: 8a19ae53e081c1a17bd3ca9b91413572781187f5
  09:33:22.266 [main] INFO  com.boozallen.aissemble.data.encryption.HashingSteps.a_data_element_is_provided():44 - data element provided
  09:33:22.323 [main] INFO  com.boozallen.aissemble.data.encryption.HashingSteps.the_data_element_is_encrypted():50 - AES ENCRYPTED DATA: pdpLbRK85Pcqo4ZG7I5spesB06D/v3TU5B7/OC053Zo/AgAqXpZaY/EhP8lVF95FBxFdJD+VG8wqECxvwnDM+pRI7fGOXCySGmk/lLUUH+jRQzQxa14/lkVSK3C3oexotZ89Z4a8FPyrVwbYiROnKzeOU81MS1KFk9DAL0+y8V8xwZ06yKP6MMrkigoS7ybUu2EeEQDOj7IPLzSZRJADjUUx6T8gtL/dUeYyO3HaWhzV1k2IjQ/IUBqVimqj74htbbEF4jN0TUPQEtptebPU1StExbbeABTIui0gZNm6UhJWyLeT5RzeRtyQEWpDm6VVuIHQ7ggGp5GPhQfSGDi5UPPSLCKiGB+ywltdOnShITi+Ws8BsRccBY+x4LFS3GLSg+T9JHcC+NBsH21z8XJmoQkVrds+ILApXsqMTsSu/b+4fPERTxMPrCsocoMj9alxgQFgJTh0NL66c4oRsqAxjP4w2bQOjAw5DR6s2sFIz8SFaJUeojFYeC/9NXravuvKaQ9txmXlXmZl4P5ECoInVp6wJZEV9Pd7DSoGtSjtwbyXp/hNVfFeG1srmNexdJxOm8bJ5NwjROcMopdiKg1oFuR8T+h9Obgz8UYtb5U1AKcgPhR2IIuYR/yC2Wtugr6ztShoMIDrXyUZxufZE5vgukSHjPZr9DpsUZa8HdFMt9qjAuX8aMRQV2lQCzsPLCkHiI9PupccVfopqnSqg63Pag==
  09:33:22.324 [main] INFO  com.boozallen.aissemble.data.encryption.HashingSteps.decrypting_it_will_return_the_original_value():74 - DECRYPTED AES DATA: Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed placerat gravida elementum. Nulla lobortis, erat vel viverra vulputate, elit tortor tristique orci, at accumsan velit massa nec nisl. Fusce consectetur consectetur ultricies. In sit amet dolor nec sapien maximus ornare. Suspendisse potenti. Morbi euismod ornare orci. Duis tempus diam vel eros ornare, ac tempus nisl scelerisque. Sed at ex et nulla porta ultricies vel viverra enim. Fusce id lacinia lectus, eget mattis magna. Sed in risus sodales, lacinia diam a, consectetur nibh.
  [INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.962 s - in com.boozallen.aissemble.data.encryption.CucumberTest
  [INFO]
  [INFO] Results:
  [INFO]
  [INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
    ```

- Python:
    ```
    [INFO] --- habushu-maven-plugin:1.1.0:test (default-test) @ aissemble-extensions-encryption-vault-python ---
  [INFO] Using python 3.11.4
  [INFO] -------------------------------------------------------
  [INFO] T E S T S
  [INFO] -------------------------------------------------------
  [INFO] @properties
  Feature: Encryption # features/encryption.feature:2
  Clearing context...
  
  @integration
  Scenario: Data can be encrypted and decrypted using Vault  # features/encryption.feature:5
  Given a plain text string                                # None
  When the string is encrypted using Vault                 # None
  Then the encrypted string can be decrypted using Vault   # None
  
  Scenario: Data can be encrypted and decrypted using AES algorithm  # features/encryption.feature:10
  Given a plain text string                                        # features/steps/encryption_steps.py:31
  When the string is encrypted using AES encryption                # features/steps/encryption_steps.py:47
  Then the encrypted string can be decrypted using AES             # features/steps/encryption_steps.py:74
  
  @integration
  Scenario: Vault encryption locally by downloading a key from the server  # features/encryption.feature:16
  Given a plain text string                                              # None
  When local vault encryption is requested                               # None
  Then a key is downloaded from the Vault server                         # None
  
  @integration
  Scenario: Local Vault encryption and decryption                     # features/encryption.feature:22
  Given a plain text string                                         # None
  When local vault encryption is requested                          # None
  Then the encrypted data can be decrypted using the local key copy # None
  
  @integration
  Scenario: Data can be encrypted and decrypted using AES GCM 96 algorithm  # features/encryption.feature:28
  Given a plain text string                                               # None
  When the string is encrypted using AES GCM 96 encryption                # None
  Then the encrypted string can be decrypted using AES GCM 96             # None
  
  1 feature passed, 0 failed, 0 skipped
  1 scenario passed, 0 failed, 4 skipped
  3 steps passed, 0 failed, 12 skipped, 0 undefined
  Took 0m0.004s
  2022/08/29 09:33:30 INFO EncryptionTests: ========= AES encryption =========
  2022/08/29 09:33:30 INFO EncryptionTests: b'PTgat/f1fzuaPKawmIx+yGpa/eDQsji4JLpa2v37I8E='
  2022/08/29 09:33:30 INFO EncryptionTests: Decrypted value: 123-4567-890
    ```


## Running integration tests
From the root of the project directory, bring up the integration test containers with the following command:

`docker-compose -f ./extensions/extensions-docker/aissemble-vault/src/main/resources/docker/docker-compose.yml up`

In a new terminal window, run the integration tests with the following command:

`./mvnw clean install -pl :extensions-encryption-vault-java,:aissemble-extensions-encryption-vault-python -Pintegration-test`

Integration tests to verify encryption using Vault are executed during the build. Check for the following output to verify the integration tests:

- Java:
    ```
  [INFO] --- maven-failsafe-plugin:3.0.0-M7:integration-test (default) @ extensions-encryption-vault-java ---
  [INFO] Using auto detected provider org.apache.maven.surefire.junit4.JUnit4Provider
  [INFO]
  [INFO] -------------------------------------------------------
  [INFO]  T E S T S
  [INFO] -------------------------------------------------------
  [INFO] Running com.boozallen.aissemble.data.encryption.CucumberIT
  09:56:23.680 [main] WARN  org.technologybrewery.krausening.Krausening.logFileDoesNotExist():195 - KRAUSENING_EXTENSIONS refers to a location that does not exist: /Users/ryan/dev/aissemble/extensions/extensions-encryption/extensions-encryption-vault-java/src/test/resources/krausening/${krausening.profile}
  09:56:23.687 [main] INFO  com.boozallen.aissemble.data.encryption.HashingSteps.a_data_element_is_provided():44 - data element provided
  09:56:23.838 [main] INFO  com.boozallen.aissemble.data.encryption.HashingSteps.the_data_element_is_encrypted_with_vault():58 - VAULT ENCRYPTED DATA: vault:v1:m9pANZKRTXd+WX1YieY/zi7W22L6GARFsHm72cyBhQICwIfEEYqMWw==
  09:56:23.848 [main] WARN  com.boozallen.aissemble.data.encryption.HashingSteps.decrypting_with_vault_it_will_return_the_original_value():81 - DECRYPTED VAULT DATA: 123-456-7890
  [INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.954 s - in com.boozallen.aissemble.data.encryption.CucumberIT
  [INFO]
  [INFO] Results:
  [INFO]
  [INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
    ```


- Python

Note the @integration annotated tests are now included:

```
  [INFO] --- habushu-maven-plugin:1.1.0:test (default-test) @ aissemble-extensions-encryption-vault-python ---
  [INFO] Using python 3.11.4
  [INFO] -------------------------------------------------------
  [INFO] T E S T S
  [INFO] -------------------------------------------------------
  [INFO] @properties
  Feature: Encryption # features/encryption.feature:2
  Clearing context...
  
  @integration
  Scenario: Data can be encrypted and decrypted using Vault  # features/encryption.feature:5
  Given a plain text string                                # features/steps/encryption_steps.py:31
  When the string is encrypted using Vault                 # features/steps/encryption_steps.py:36
  Then the encrypted string can be decrypted using Vault   # features/steps/encryption_steps.py:67
  Clearing context...
  
  Scenario: Data can be encrypted and decrypted using AES algorithm  # features/encryption.feature:10
  Given a plain text string                                        # None
  When the string is encrypted using AES encryption                # None
  Then the encrypted string can be decrypted using AES             # None
  
  @integration
  Scenario: Vault encryption locally by downloading a key from the server  # features/encryption.feature:16
  Given a plain text string                                              # features/steps/encryption_steps.py:31
  When local vault encryption is requested                               # features/steps/encryption_steps.py:57
  Then a key is downloaded from the Vault server                         # features/steps/encryption_steps.py:82
  Clearing context...
  
  @integration
  Scenario: Local Vault encryption and decryption                     # features/encryption.feature:22
  Given a plain text string                                         # features/steps/encryption_steps.py:31
  When local vault encryption is requested                          # features/steps/encryption_steps.py:57
  Then the encrypted data can be decrypted using the local key copy # features/steps/encryption_steps.py:87
  Clearing context...
  
  @integration
  Scenario: Data can be encrypted and decrypted using AES GCM 96 algorithm  # features/encryption.feature:28
  Given a plain text string                                               # features/steps/encryption_steps.py:31
  When the string is encrypted using AES GCM 96 encryption                # features/steps/encryption_steps.py:94
  Then the encrypted string can be decrypted using AES GCM 96             # features/steps/encryption_steps.py:106
  
  1 feature passed, 0 failed, 0 skipped
  4 scenarios passed, 0 failed, 1 skipped
  12 steps passed, 0 failed, 3 skipped, 0 undefined
  Took 0m0.100s
    ```

When finished, stop the integration test containers with the following command:

`docker-compose -f ./extensions/extensions-docker/aissemble-vault/src/main/resources/docker/docker-compose.yml down`
