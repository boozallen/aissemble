# Policy Decision Point Docker

Builds a docker container that deploys the artifacts from the Policy Decision Point service module
on a Quarkus server.

To start this container run

`docker run -p 8780:8080 boozallen/aissemble-policy-decision-point:AISSEMBLE_VERSION`

substituting the current aiSSEMBLE version for `AISSEMBLE_VERSION`, ie 1.2.0-SNAPSHOT

To make a call to the decision point use the following REST endpoint

POST localhost:8780/api/pdp

Body:
``` json
{
  "jwt": "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJqdGkiOiI3ZDZmMWZlNS05YzZiLTQ1ZGEtODlmMS0yMDM5YWIwNWZhNDEiLCJzdWIiOiJhaW9wcyIsImF1ZCI6ImF1ZGllbmNlIiwibmJmIjoxNjI0OTc2MDI5LCJpYXQiOjE2MjQ5NzYwMjksImV4cCI6MTkyNDk4MDc5NywiaXNzIjoiYWlvcHMuYXV0aG9yaXR5In0.eUBC2ink77XRf5n5JIXlLZR-fBiRmGrqo1TBFz46yZhWDY38dsh30flELE8gO5SG2rUSIe-VmmjSny8PFLNGwy5MGLwr9z56HoH7OrejJeEzCa1yBl67VWgUZhoDy3RzvARfdBnUstfigHYeQA2ECvW-b2kppYJPVUNX2uKmwfZupwqCGqIX56s7qntV0dUAjpC_KiZ3fjUz1HXqK_evWos0xPVT8XOB2ZADhh87kf7LmocYQ4Y-Z_fsou6jqYh1lQT8WeI2AKskE613nSqmTA2bax5-dOFXKWKLy8t5glyjkdqFZVrLrNkK9tXqNYpZ8efIkZKOu7T9TlsvkHU1XQ",
  "resource": null,
  "action": "data-access"
}
```
**Note:** The provided jwt has a long expiration date and is valid for several years.  Typically, you would configure 
your security policies to limit how long a token can be valid and require a new token on a regular basis. To generate 
a new token you can send a GET request to http://localhost:8080/api/token which will return a valid token.  You can 
then substitute this token in the example json above.


* jwt
    * A json web token which contains the subject (sub) and other attributes.
* resource
    * Used if you need a policy decision for accessing a resource.
* action
    * Used if you need a policy decision for performing an action.


The default policy `test-policy.xml` will return a "PERMIT" decision if the subject has an attribute 
`urn:aiops:accessData` with a value of true. Conversely, it will return a "DENY" decision of the same attribute is false.

For this example we are using a local attribute store `LocalAttributePoint.java` which will look up attributes for 
a given subject (from the jwt) and return the matching attributes.  
 