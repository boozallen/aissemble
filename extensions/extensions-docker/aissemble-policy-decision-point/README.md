# Policy Decision Point Docker

Builds a docker container that deploys the artifacts from the Policy Decision Point service module
on a Quarkus server.

To start this container run

`docker run -p 8780:8080 ghcr.io/boozallen/aissemble-policy-decision-point:AISSEMBLE_VERSION`

substituting the current aiSSEMBLE&trade; version for `AISSEMBLE_VERSION`, ie 1.2.0-SNAPSHOT

To make a call to the decision point use the following REST endpoint

POST localhost:8780/api/pdp

Body:
``` json
{
  "jwt": "eyJhbGciOiJSUzI1NiJ9.eyJqdGkiOiI0ZDdmZTRkYy00MjI5LTQxMzAtODczMi00ZTUwY2FlYmRlOTIiLCJzdWIiOiJhaXNzZW1ibGUiLCJhdWQiOiJhdWRpZW5jZSIsIm5iZiI6MTcyMjU0MjUzMiwiaWF0IjoxNzIyNTQyNTkyLCJleHAiOjE3MjI1NDYyNTIsImlzcyI6ImFpb3BzLmF1dGhvcml0eSJ9.byliAiYHgapHVHi5dl2YjzkhlUBiPXhpsvCcEIFqTIdvs6ruGYJt7a-TxG5wLRMtUA34k0fNVKVAtNAb50P8bh60FphjI9PwUwjiYio1Cek9zJbQRz8gB-FMWwOX1dsOAt4XRiUDJHeuV_Rok4fa1p0CZJeuVRAW4YSb1v_jhN-mBg59jYbOM5kXuorZ8vG-rXcl2JYCCojmDOeW48WcCivhmUq70KTZzvhSS43DeCSGF4m20-hzjNOrZfwZC6OOYg0huiETZTXq2zpFXVGlyYMlheR5Dhn79GDKvs0N42TyGfSKfXVI5oCYlUj7he9Yous9zK2vFfZYgWQtYmf0Dw",
  "resource": "*",
  "action": "data-access"
}
```
**Note:** The provided jwt is just an example and is expired. To generate a new token you can send a POST request to
http://localhost:8080/api/authenticate with a `"username"` and `"password"`. The default example authentication mechanism
simply returns a valid token for the given username. This can be replaced with, e.g., a Keycloak backed secure token
service. Once you have a valid JWT, you can then substitute this token in the example json above.


* jwt
    * A json web token which contains the subject (sub) and other attributes.
* resource
    * Used if you need a policy decision for accessing a resource.
* action
    * Used if you need a policy decision for performing an action.


The default policy `test-policy.xml` will return a "PERMIT" decision for the `data-access` action if the subject has an
attribute `urn:aissemble:accessData` with a value of true. Conversely, it will return a "DENY" decision of the same
attribute is false. For this example we are using a local attribute store `LocalAttributePoint.java` which will look up
attributes for a given subject (from the jwt) and return the matching attributes.  This simple implementation simply
sets the attribute to true if the subject (the user for which the token was created) is `aissemble` and false otherwise.
For any actions other than `data-access` the policy will return a "NOT_APPLICABLE" decision.
 